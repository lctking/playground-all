package com.nullwli.itemservice.remote.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatusEnum {

    /**
     * 待支付：用户选好商品下单，但还未付款的状态
     */
    PENDING_PAYMENT(0),

    /**
     * 已支付：用户支付订单费用
     */
    ALREADY_PAID(10),

    /**
     * 部分退款：用户支付订单费用后部分商品退款
     */
    PARTIAL_REFUND(11),

    /**
     * 全部退款：用户支付订单费用后全部商品退款
     */
    FULL_REFUND(12),

    /**
     * 已完成：超过退款期限，订单完成
     */
    COMPLETED(20),

    /**
     * 已取消：用户选好商品下单，未支付状态下取消订单
     */
    CLOSED(30);

    private final int status;
}
