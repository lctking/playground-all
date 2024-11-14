package com.nullwli.itemservice.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.nullwli.itemservice.dao.entity.ItemDO;
import com.nullwli.itemservice.dao.mapper.ItemMapper;
import com.nullwli.itemservice.dto.domain.DiscountDetailsDTO;
import com.nullwli.itemservice.dto.domain.ItemPurchaseDetailDTO;
import com.nullwli.itemservice.dto.req.ItemPurchaseDetailReqDTO;
import com.nullwli.itemservice.dto.req.ItemCreateReqDTO;
import com.nullwli.itemservice.dto.req.ItemsPurchaseReqDTO;
import com.nullwli.itemservice.dto.domain.ItemsPurchaserDetailDTO;
import com.nullwli.itemservice.dto.resp.ItemCreateRespDTO;
import com.nullwli.itemservice.dto.resp.ItemPurchaseDetailRespDTO;
import com.nullwli.itemservice.dto.resp.ItemQueryRespDTO;
import com.nullwli.itemservice.dto.resp.ItemsPurchaseRespDTO;
import com.nullwli.itemservice.remote.AddressRemoteService;
import com.nullwli.itemservice.remote.OrderRemoteService;
import com.nullwli.itemservice.remote.dto.AddressQueryRespDTO;
import com.nullwli.itemservice.remote.dto.OrderCreateReqDTO;
import com.nullwli.itemservice.service.ItemService;
import com.nullwli.itemservice.strategy.ItemDiscountStrategy;
import com.nullwli.itemservice.strategy.impl.FixedValueDiscountStrategy;
import com.nullwli.itemservice.strategy.impl.PercentageDiscountStrategy;
import com.nullwli.itemservice.strategy.impl.ThresholdDiscountStrategy;
import com.nullwli.playground.frameworks.starter.convention.bases.Singleton;
import com.nullwli.playground.frameworks.starter.convention.utils.Assert;
import com.nullwli.playground.frameworks.starter.convention.utils.BeanTools;
import com.nullwli.playground.frameworks.starter.user.core.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl extends ServiceImpl<ItemMapper, ItemDO> implements ItemService {

    private final ItemMapper itemMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final AddressRemoteService addressRemoteService;
    private final OrderRemoteService orderRemoteService;
    private final RedissonClient redissonClient;
    private static final String ITEMS_PURCHASE_STOCK_BUCKET_SCRIPT_PATH = "lua/items_purchase_stock_bucket.lua";
    private static final String ITEMS_PURCHASE_STOCK_ROLLBACK_SCRIPT_PATH = "lua/items_purchase_stock_rollback.lua";
    private static final String ITEMS_PURCHASE_STOCK_BUCKET_PREFIX = "item-service:items_purchase_stock_bucket";

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ItemCreateRespDTO ItemCreate(ItemCreateReqDTO requestPram) throws Exception {
        //检查requestPram参数是否合规（空值、价格非负、内容安全
        if(requestPram.getItemName().isEmpty()){
            throw new Exception("商品名为空！");//有待自定义exception优化
        }
        if(requestPram.getPrice()<=0){
            throw new Exception("价格<=0！");//有待自定义exception优化
        }
        if(requestPram.getStock()<0){
            throw new Exception("库存<0！");//有待自定义exception优化
        }
        //构造实体，插入数据库
        ItemDO itemDO = BeanTools.convert(requestPram, ItemDO.class);

        String itemIdStr;
        try{
            itemMapper.insert(itemDO);
            itemIdStr = String.valueOf(itemDO.getId());
            //拼接令牌桶key
            String key = ITEMS_PURCHASE_STOCK_BUCKET_PREFIX + ":" + itemIdStr.substring(itemIdStr.length()-4) + ":" + itemIdStr;
            stringRedisTemplate.opsForHash().put(key,"stock", String.valueOf(requestPram.getStock()));
        }catch (Exception e){
            throw new Exception("商品信息插入失败！");//有待自定义exception优化
        }


        return ItemCreateRespDTO.builder().itemId(itemIdStr).build();
    }

    @Override
    public ItemQueryRespDTO SelectItemById(String itemId) throws Exception {
        long id = -1L;
        //格式转换+勘误
        try{
            id = Long.parseLong(itemId);
        }catch (NumberFormatException e){
            throw new NumberFormatException("id格式有误！");//有待自定义exception优化
        }
        if(id<=0){
            throw new Exception("id<=0！");//有待自定义exception优化
        }
        //查询+排空
        ItemDO itemDO = itemMapper.selectById(id);
        if(Objects.isNull(itemDO)){
            return null;
        }
        //格式转换+设置id（因该两类id数据类型不同
        ItemQueryRespDTO itemSelectRespDTO = BeanTools.convert(itemDO, ItemQueryRespDTO.class);
        itemSelectRespDTO.setId(itemId);
        return itemSelectRespDTO;

    }

    private final Cache<String, ReentrantLock> localLockMap = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build();

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ItemsPurchaseRespDTO purchaseItems(ItemsPurchaseReqDTO requestPram) throws Exception {

        //远程获取下单人地址信息
        AddressQueryRespDTO addressDetail = addressRemoteService.AddressQuery(requestPram.getPurchaserAddressId()).getData();
        //下单人信息赋值
        ItemsPurchaserDetailDTO purchaserDetail = BeanTools.convert(addressDetail,ItemsPurchaserDetailDTO.class);
        //获取下单商品信息
        List<ItemPurchaseDetailReqDTO> itemsDetails = requestPram.getItemsDetails();
        //获取折扣信息
        DiscountDetailsDTO discountDetails = requestPram.getDiscountDetailsDTO();
        List<ItemPurchaseDetailRespDTO> itemOrderDetails = new ArrayList<>();

        int totalPrice = 0,actualTotalPrice = 0;

        for(ItemPurchaseDetailReqDTO e : itemsDetails){
            long itemId = Long.parseLong(e.getItemId());
            int amount = e.getAmount();
            ItemDO itemDO = itemMapper.selectById(itemId);
            ItemPurchaseDetailRespDTO itemPurchaseDetailRespDTO = ItemPurchaseDetailRespDTO.builder()
                    .itemName(itemDO.getItemName())
                    .price(itemDO.getPrice() * amount)
                    .actualPrice(itemDO.getActualPrice() * amount)
                    .amount(amount)
                    .build();
            itemOrderDetails.add(itemPurchaseDetailRespDTO);
            totalPrice += itemDO.getPrice() * amount;
            actualTotalPrice += itemDO.getActualPrice() * amount;
        }

        ItemsPurchaseRespDTO itemsPurchaseRespDTO = ItemsPurchaseRespDTO.builder()
                .itemOrderDetails(itemOrderDetails)
                .totalPrice(totalPrice)
                .actualTotalPrice(actualTotalPrice)
                .purchaserDetail(purchaserDetail)
                .build();
        itemsPurchaseRespDTO.setActualWithDiscountTotalPrice(calculateFinalPrice(discountDetails,actualTotalPrice));

        //进一步计算（加权平均）每个商品的实付价格（actualWithDiscountPrice）
        double ratio = (double) itemsPurchaseRespDTO.getActualWithDiscountTotalPrice() / (double) actualTotalPrice;
        for(ItemPurchaseDetailRespDTO e : itemOrderDetails){
            e.setActualWithDiscountPrice((int)(e.getActualPrice() * ratio));
        }

        //本地锁
        List<ReentrantLock> localLockList = new ArrayList<>();
        List<RLock> distributedLockList = new ArrayList<>();
        for(ItemPurchaseDetailReqDTO e:itemsDetails){
            String itemSingleKey = String.format("item:purchase:lock:local:%s",e.getItemId());
            ReentrantLock lock = localLockMap.getIfPresent(itemSingleKey);
            if(lock == null){
                synchronized (ItemService.class){
                    if(localLockMap.getIfPresent(itemSingleKey) == null){
                        lock = new ReentrantLock(true);
                        localLockMap.put(itemSingleKey,lock);
                    }
                }
            }
            localLockList.add(lock);
            RLock distributedLock = redissonClient.getFairLock(itemSingleKey);
            distributedLockList.add(distributedLock);
        }


        try{
            localLockList.forEach(ReentrantLock::lock);
            distributedLockList.forEach(RLock::lock);

            //TODO 扣减库存
            //redis-令牌限流
            //获取lua脚本
            DefaultRedisScript<String> itemsDeductScript = Singleton.get(ITEMS_PURCHASE_STOCK_BUCKET_SCRIPT_PATH, () -> {
                DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
                redisScript.setResultType(String.class);
                redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(ITEMS_PURCHASE_STOCK_BUCKET_SCRIPT_PATH)));
                return redisScript;
            });
            Assert.notNull(itemsDeductScript);

            String result = stringRedisTemplate.execute(itemsDeductScript, Lists.newArrayList(ITEMS_PURCHASE_STOCK_BUCKET_PREFIX), JSON.toJSONString(itemsDetails));
            if (!(result != null && result.equals("success"))) {
                throw new Exception("获取令牌失败"+result);
            }
            try{
                //测试redis回滚成功
//            Thread.sleep(9000);
//            if(1==1)throw new Exception("");
                log.info("");
                //mysql库存扣减
                for(ItemPurchaseDetailReqDTO e : itemsDetails){
                    RLock lock = redissonClient.getLock("item:purchase:lock:mysql" + e.getItemId());
                    lock.lock();
                    try{
                        LambdaUpdateWrapper<ItemDO> itemDOLambdaUpdateWrapper = Wrappers.lambdaUpdate(ItemDO.class).eq(ItemDO::getId, Long.parseLong(e.getItemId()))
                                .setSql("stock = stock -" + e.getAmount());
                        int update = itemMapper.update(itemDOLambdaUpdateWrapper);
                        if(update < 1){
                            throw new Exception("库存扣减失败");
                        }
                    }finally {
                        lock.unlock();
                    }
                }

                // 订单生成 依靠order-service模块实现
                //生成订单详情信息（各个商品的购买/价格情况
                List<ItemPurchaseDetailDTO> itemPurchaseDetails = new ArrayList<>();
                for(int i=0;i<itemsDetails.size();i++){
                    ItemPurchaseDetailRespDTO source = itemOrderDetails.get(i);
                    ItemPurchaseDetailDTO target = ItemPurchaseDetailDTO.builder()
                            .price(source.getPrice())
                            .itemId(itemsDetails.get(i).getItemId())
                            .itemName(source.getItemName())
                            .amount(source.getAmount())
                            .actualWithDiscountPrice(source.getActualWithDiscountPrice()).build();
                    itemPurchaseDetails.add(target);
                }

                //生成订单创建请求信息
                OrderCreateReqDTO orderCreateReqDTO = OrderCreateReqDTO.builder()
                        .userId(UserContext.getUserId())
                        .addressId(requestPram.getPurchaserAddressId())
                        .username(UserContext.getUsername())
                        .orderTime(new Date())
                        .originPrice(itemsPurchaseRespDTO.getTotalPrice())
                        .itemPurchaseDetails(itemPurchaseDetails)
                        .amountPaid(itemsPurchaseRespDTO.getActualWithDiscountTotalPrice()).build();
                //发送订单创建请求
                String orderSn = orderRemoteService.OrderCreate(orderCreateReqDTO).getData().getOrderSn();
                if(orderSn == null || orderSn.isEmpty()){
                    throw new Exception("订单生成失败");
                }

                itemsPurchaseRespDTO.setOrderSn(orderSn);
            }catch(Throwable e){
                //e.printStackTrace();
                //redis-令牌回滚
                //获取lua脚本
                DefaultRedisScript<String> itemsRollbackScript = Singleton.get(ITEMS_PURCHASE_STOCK_ROLLBACK_SCRIPT_PATH, () -> {
                    DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
                    redisScript.setResultType(String.class);
                    redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(ITEMS_PURCHASE_STOCK_ROLLBACK_SCRIPT_PATH)));
                    return redisScript;
                });
                Assert.notNull(itemsRollbackScript);

                String rollbackResult = stringRedisTemplate.execute(itemsRollbackScript, Lists.newArrayList(ITEMS_PURCHASE_STOCK_BUCKET_PREFIX), JSON.toJSONString(itemsDetails));
                if (!(rollbackResult != null && rollbackResult.equals("success"))) {
                    throw new Exception("回滚redis令牌失败"+rollbackResult);
                }
                throw new Exception("执行redis回滚");
            }

            return itemsPurchaseRespDTO;
        }finally {
            localLockList.forEach(localLock ->{
                try{
                    localLock.unlock();
                }catch (Throwable ignored){}
            });
            distributedLockList.forEach(distributedLock ->{
                try{
                    distributedLock.unlock();
                }catch (Throwable ignored){}
            });


        }






    }

    /**
     * 商品流量防护：输入itemId查询mysql得到库存量，将其放入/更新redis令牌桶中
     * @param itemId
     */
    @Override
    public void itemFlowProtection(String itemId) {
        //首先查询mysql得到实体信息
        ItemDO itemDO = itemMapper.selectById(Long.parseLong(itemId));
        int stock = itemDO.getStock();
        if(stock < 0){
            stock = 0;
        }
        //拼接令牌桶key
        String key = ITEMS_PURCHASE_STOCK_BUCKET_PREFIX + ":" + itemId.substring(itemId.length()-4) + ":" + itemId;
        stringRedisTemplate.opsForHash().put(key,"stock", String.valueOf(stock));
    }

    //TODO 回滚测试成功，下一步将回滚逻辑加入到purchaseItems方法中
    @Override
    public ItemsPurchaseRespDTO testRedisRollback(ItemsPurchaseReqDTO requestPram) throws Exception {
        List<ItemPurchaseDetailReqDTO> itemsDetails = requestPram.getItemsDetails();
        //redis-令牌回滚
        //获取lua脚本
        DefaultRedisScript<String> itemsRollbackScript = Singleton.get(ITEMS_PURCHASE_STOCK_ROLLBACK_SCRIPT_PATH, () -> {
            DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
            redisScript.setResultType(String.class);
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(ITEMS_PURCHASE_STOCK_ROLLBACK_SCRIPT_PATH)));
            return redisScript;
        });
        Assert.notNull(itemsRollbackScript);

        String result = stringRedisTemplate.execute(itemsRollbackScript, Lists.newArrayList(ITEMS_PURCHASE_STOCK_BUCKET_PREFIX), JSON.toJSONString(itemsDetails));
        if (!(result != null && result.equals("success"))) {
            throw new Exception("回滚令牌失败"+result);
        }
        return null;
    }


    private int calculateFinalPrice(DiscountDetailsDTO discountDetails,int price) throws Exception {
        // 根据折扣类型应用相应的折扣
        ItemDiscountStrategy itemDiscountStrategy;
        int value = discountDetails.getDiscountValue();
        itemDiscountStrategy = switch (discountDetails.getDiscountType()) {
            case "percentage" -> new PercentageDiscountStrategy(value);
            case "fixed" -> new FixedValueDiscountStrategy(value);
            case "threshold" -> new ThresholdDiscountStrategy(discountDetails.getDiscountThreshold(), value);
            default -> throw new IllegalArgumentException("Invalid discount type");
        };
        return itemDiscountStrategy.applyDiscount(price);

    }
}
