package com.nullwli.itemservice.service.handler.item.tokenbucket;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.nullwli.itemservice.dao.mapper.ItemMapper;
import com.nullwli.itemservice.dto.req.ItemPurchaseDetailReqDTO;
import com.nullwli.itemservice.dto.req.ItemsPurchaseReqDTO;
import com.nullwli.itemservice.remote.AddressRemoteService;
import com.nullwli.itemservice.remote.OrderRemoteService;
import com.nullwli.itemservice.service.handler.item.dto.TokenResultDTO;
import com.nullwli.playground.frameworks.starter.convention.bases.Singleton;
import com.nullwli.playground.frameworks.starter.convention.utils.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public final class ItemAvailabilityTokenBucket {
    private final ItemMapper itemMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final AddressRemoteService addressRemoteService;
    private final OrderRemoteService orderRemoteService;
    private final RedissonClient redissonClient;
    private static final String ITEMS_PURCHASE_STOCK_BUCKET_SCRIPT_PATH = "lua/items_purchase_stock_bucket.lua";
    private static final String ITEMS_PURCHASE_STOCK_ROLLBACK_SCRIPT_PATH = "lua/items_purchase_stock_rollback.lua";
    private static final String ITEMS_PURCHASE_STOCK_BUCKET_PREFIX = "item-service:items_purchase_stock_bucket";

    //TODO 将该对象整合进现有购物逻辑中
    public TokenResultDTO takeTokenFromBucket(ItemsPurchaseReqDTO requestPram){

        //获取下单商品信息
        List<ItemPurchaseDetailReqDTO> itemsDetails = requestPram.getItemsDetails();
        //1，redis-令牌限流
        //1.1 获取lua脚本
        DefaultRedisScript<String> itemsDeductScript = Singleton.get(ITEMS_PURCHASE_STOCK_BUCKET_SCRIPT_PATH, () -> {
            DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
            redisScript.setResultType(String.class);
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(ITEMS_PURCHASE_STOCK_BUCKET_SCRIPT_PATH)));
            return redisScript;
        });
        Assert.notNull(itemsDeductScript);

        String result = stringRedisTemplate.execute(itemsDeductScript, Lists.newArrayList(ITEMS_PURCHASE_STOCK_BUCKET_PREFIX), JSON.toJSONString(itemsDetails));
        TokenResultDTO tokenResult = new TokenResultDTO(true);
        if (result == null || !result.equals("success")) {
            tokenResult.setIsSuccess(false);
        }
        return tokenResult;
    }

    public void rollbackToken(ItemsPurchaseReqDTO requestPram) throws Exception {
        //获取下单商品信息
        List<ItemPurchaseDetailReqDTO> itemsDetails = requestPram.getItemsDetails();
        //执行redis-令牌回滚
        //获取lua脚本
        DefaultRedisScript<String> itemsRollbackScript = Singleton.get(ITEMS_PURCHASE_STOCK_ROLLBACK_SCRIPT_PATH, () -> {
            DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
            redisScript.setResultType(String.class);
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(ITEMS_PURCHASE_STOCK_ROLLBACK_SCRIPT_PATH)));
            return redisScript;
        });
        Assert.notNull(itemsRollbackScript);
        String result = stringRedisTemplate.execute(itemsRollbackScript, Lists.newArrayList(ITEMS_PURCHASE_STOCK_BUCKET_PREFIX), JSON.toJSONString(itemsDetails));
        if (result == null || result.equals("success")) {
            log.error("回滚列车余票令牌失败，订单信息：{}", JSON.toJSONString(requestPram));
            throw new Exception("回滚列车余票令牌失败");
        }

    }


}
