

package com.nullwli.orderservice.mq.consumer;

import com.nullwli.orderservice.common.constant.OrderRocketMQConstant;
import com.nullwli.orderservice.mq.domain.MessageWrapper;
import com.nullwli.orderservice.mq.event.PayResultCallbackOrderEvent;
import com.nullwli.orderservice.service.OrderService;
import com.nullwli.playground.frameworks.starter.idempotent.annotation.Idempotent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 支付结果回调订单消费者
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：12306）获取项目资料
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = OrderRocketMQConstant.PAY_GLOBAL_TOPIC_KEY,
        selectorExpression = OrderRocketMQConstant.PAY_RESULT_CALLBACK_TAG_KEY,
        consumerGroup = OrderRocketMQConstant.PAY_RESULT_CALLBACK_ORDER_CG_KEY
)
public class PayResultCallbackOrderConsumer implements RocketMQListener<MessageWrapper<PayResultCallbackOrderEvent>> {

    private final OrderService orderService;

    @Idempotent(
            uniqueKeyPrefix = "playground-order:pay_result_callback:",
            key = "#message.getKeys()+'_'+#message.hashCode()",
            keyTimeout = 7200L
    )
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void onMessage(MessageWrapper<PayResultCallbackOrderEvent> message) {
//        PayResultCallbackOrderEvent payResultCallbackOrderEvent = message.getMessage();
//        OrderStatusReversalDTO orderStatusReversalDTO = OrderStatusReversalDTO.builder()
//                .orderSn(payResultCallbackOrderEvent.getOrderSn())
//                .orderStatus(OrderStatusEnum.ALREADY_PAID.getStatus())
//                .orderItemStatus(OrderItemStatusEnum.ALREADY_PAID.getStatus())
//                .build();
//        orderService.statusReversal(orderStatusReversalDTO);
//        orderService.payCallbackOrder(payResultCallbackOrderEvent);
    }
}
