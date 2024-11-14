
package com.nullwli.itemservice.mq.consumer;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.nullwli.itemservice.common.constant.ItemRocketMQConstant;
import com.nullwli.itemservice.dao.entity.ItemDO;
import com.nullwli.itemservice.dao.mapper.ItemMapper;
import com.nullwli.itemservice.dto.req.ItemPurchaseDetailReqDTO;
import com.nullwli.itemservice.mq.domain.MessageWrapper;
import com.nullwli.itemservice.mq.event.DelayCloseOrderEvent;
import com.nullwli.itemservice.remote.OrderRemoteService;
import com.nullwli.itemservice.remote.dto.OrderStatusUpdateReqDTO;
import com.nullwli.itemservice.remote.enums.OrderStatusEnum;
import com.nullwli.playground.frameworks.starter.convention.bases.Singleton;
import com.nullwli.playground.frameworks.starter.convention.utils.Assert;
import com.nullwli.playground.frameworks.starter.idempotent.annotation.Idempotent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 延迟关闭订单消费者
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：12306）获取项目资料
 */
@Slf4j
@Component//PullBatchSize
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = ItemRocketMQConstant.ORDER_DELAY_CLOSE_TOPIC_KEY,
        selectorExpression = ItemRocketMQConstant.ORDER_DELAY_CLOSE_TAG_KEY,
        consumerGroup = ItemRocketMQConstant.TICKET_DELAY_CLOSE_CG_KEY
)
public class DelayCloseOrderConsumer implements RocketMQListener<MessageWrapper<DelayCloseOrderEvent>> {
    private final OrderRemoteService orderRemoteService;
    private final StringRedisTemplate stringRedisTemplate;
    private final ItemMapper itemMapper;
    private final RedissonClient redissonClient;
    private static final String ITEMS_PURCHASE_STOCK_ROLLBACK_SCRIPT_PATH = "lua/items_purchase_stock_rollback.lua";
    private static final String ITEMS_PURCHASE_STOCK_BUCKET_PREFIX = "item-service:items_purchase_stock_bucket";




    @SneakyThrows(value = Throwable.class)
    @Idempotent(
            uniqueKeyPrefix = "playground-item:delay_close_order",
            key = "#delayCloseOrderEventMessageWrapper.getKeys()+'_'+#delayCloseOrderEventMessageWrapper.hashCode()",
            keyTimeout = 7200L
    )
    @Override
    public void onMessage(MessageWrapper<DelayCloseOrderEvent> delayCloseOrderEventMessageWrapper) {
        log.info("[延迟关闭订单] 开始消费：{}", JSON.toJSONString(delayCloseOrderEventMessageWrapper));
        DelayCloseOrderEvent delayCloseOrderEvent = delayCloseOrderEventMessageWrapper.getMessage();
        String orderSn = delayCloseOrderEvent.getOrderSn();
        List<ItemPurchaseDetailReqDTO> itemsDetails = delayCloseOrderEvent.getItemsPurchaseDetails();
        //关闭订单（设置订单状态为取消支付），回滚mysql库存，回滚redis
        try{
            orderRemoteService.updateStatus(OrderStatusUpdateReqDTO.builder()
                            .orderSn(orderSn)
                            .status(OrderStatusEnum.CLOSED.getStatus())
                            .build());
        }catch (Throwable ex) {
            log.error("[延迟关闭订单] 订单号：{} 远程调用订单服务失败", orderSn, ex);
            throw ex;
        }
        try{
            for(ItemPurchaseDetailReqDTO e : itemsDetails){
                RLock lock = redissonClient.getLock("item:purchase:lock:mysql" + e.getItemId());
                lock.lock();
                try{
                    LambdaUpdateWrapper<ItemDO> itemDOLambdaUpdateWrapper = Wrappers.lambdaUpdate(ItemDO.class).eq(ItemDO::getId, Long.parseLong(e.getItemId()))
                            .setSql("stock = stock +" + e.getAmount());
                    int update = itemMapper.update(itemDOLambdaUpdateWrapper);
                    if(update < 1){
                        throw new Exception("库存扣减失败");
                    }
                }finally {
                    lock.unlock();
                }
            }
        }catch (Throwable ex){
            log.error("[延迟关闭订单] 订单号：{} mysql库存回滚失败", orderSn, ex);
            throw ex;
        }


        try{
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
        }catch (Throwable ex){
            log.error("[延迟关闭订单] 订单号：{} redis令牌回滚失败", orderSn, ex);
            throw ex;
        }


    }
}
