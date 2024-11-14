package com.nullwli.orderservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nullwli.orderservice.common.enums.OrderStatusEnum;
import com.nullwli.orderservice.dao.entity.OrderDO;
import com.nullwli.orderservice.dao.entity.OrderDetailDO;
import com.nullwli.orderservice.dao.mapper.OrderDetailMapper;
import com.nullwli.orderservice.dao.mapper.OrderMapper;
import com.nullwli.orderservice.dto.req.OrderCreateReqDTO;
import com.nullwli.orderservice.dto.req.OrderStatusUpdateReqDTO;
import com.nullwli.orderservice.dto.req.OrderUpdateReqDTO;
import com.nullwli.orderservice.dto.resp.OrderCreateRespDTO;
import com.nullwli.orderservice.mq.event.DelayCloseOrderEvent;
import com.nullwli.orderservice.mq.produce.DelayCloseOrderSendProduce;
import com.nullwli.orderservice.remote.dto.ItemPurchaseDetailDTO;
import com.nullwli.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderDO> implements OrderService {
    private final OrderDetailMapper orderDetailMapper;

    private final OrderMapper orderMapper;

    private final DelayCloseOrderSendProduce delayCloseOrderSendProduce;
    @Override
    public OrderCreateRespDTO OrderCreate(OrderCreateReqDTO requestPram) throws Exception {
        //生成订单号
        String orderSn = generateOrderSn(requestPram.getUserId());
        if (orderSn == null || orderSn.isEmpty()) {
            throw new Exception("订单号生成失败");
        }

        List<ItemPurchaseDetailDTO> itemPurchaseDetails = requestPram.getItemPurchaseDetails();
        //写入orderDetail表
        for(ItemPurchaseDetailDTO e : itemPurchaseDetails){
            OrderDetailDO orderDetailDO = OrderDetailDO.builder()
                    .orderSn(orderSn)
                    .itemId(Long.parseLong(e.getItemId()))
                    .itemName(e.getItemName())
                    .itemAmount(e.getAmount())
                    .originPrice(e.getPrice())
                    .amountPaid(e.getActualWithDiscountPrice())
                    .status(0)//未支付
                    .build();
            orderDetailMapper.insert(orderDetailDO);
        }
        //写入Order表
        OrderDO orderDO = OrderDO.builder()
                .orderSn(orderSn)
                .userId(Long.parseLong(requestPram.getUserId()))
                .addressId(Long.parseLong(requestPram.getAddressId()))
                .username(requestPram.getUsername())
                .status(OrderStatusEnum.PENDING_PAYMENT.getStatus())
                .orderTime(new Date())
                .build();
        orderMapper.insert(orderDO);


        return OrderCreateRespDTO.builder().orderSn(orderSn).build();
    }

    @SneakyThrows(value = Exception.class)
    @Override
    public void payCallbackUpdate(OrderUpdateReqDTO requestPram) {
        LambdaUpdateWrapper<OrderDO> orderUpdate = Wrappers.lambdaUpdate(OrderDO.class)
                .eq(OrderDO::getOrderSn, requestPram.getOrderSn())
                .set(OrderDO::getPayType, requestPram.getPayType())
                .set(OrderDO::getStatus, requestPram.getStatus())
                .set(OrderDO::getPayTime, requestPram.getPayTime());
        LambdaUpdateWrapper<OrderDetailDO> orderDetailUpdate = Wrappers.lambdaUpdate(OrderDetailDO.class)
                .eq(OrderDetailDO::getOrderSn, requestPram.getOrderSn())
                .set(OrderDetailDO::getStatus, requestPram.getStatus());
        orderMapper.update(orderUpdate);
        orderDetailMapper.update(orderDetailUpdate);
    }

    @Override
    public void testDelayCloseOrder(DelayCloseOrderEvent requestPram) {
        SendResult result = delayCloseOrderSendProduce.sendMessage(requestPram);
        log.info("test-delay-close-order send-message返回结果:{}",result);
    }

    @Override
    public void updateStatus(OrderStatusUpdateReqDTO requestPram) {
        LambdaUpdateWrapper<OrderDO> orderUpdate = Wrappers.lambdaUpdate(OrderDO.class)
                .eq(OrderDO::getOrderSn, requestPram.getOrderSn())
                .set(OrderDO::getStatus, requestPram.getStatus());
        orderMapper.update(orderUpdate);
    }

    /**
     * 生成19位订单号order_sn 并将userId后六位冗余到订单号
     * @param userId
     * @return
     */
    private String generateOrderSn(String userId){
        if(userId == null || userId.isEmpty())return null;
        int len = userId.length();
        if(len < 6)return null;
        return userId.substring(len-6,len)+generateRandomNums(13);

    }
    private String generateRandomNums(int len){
        StringBuilder randomDigits = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < len; i++) {
            randomDigits.append(random.nextInt(10)); // 生成0-9之间的随机数字
        }
        return randomDigits.toString();
    }

}
