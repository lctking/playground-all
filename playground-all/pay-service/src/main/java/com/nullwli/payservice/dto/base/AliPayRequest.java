package com.nullwli.payservice.dto.base;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Accessors(chain = true)
public final class AliPayRequest {

    /**
     * 商户订单号
     * 由商家自定义，64个字符以内，仅支持字母、数字、下划线且需保证在商户端不重复
     */
    private String outOrderSn;

    /**
     * 订单总金额
     * 单位为元，精确到小数点后两位，取值范围：[0.01,100000000]
     */
    private BigDecimal totalAmount;

    /**
     * 订单标题
     * 注意：不可使用特殊字符，如 /，=，& 等
     */
    private String subject;

    /**
     * 交易凭证号
     */
    private String tradeNo;

    /**
     * 交易环境，H5、小程序、网站等
     */
    private Integer tradeType;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 支付渠道
     */
    private Integer channel;

    /**
     * 商户订单号
     * 由商家自定义，64个字符以内，仅支持字母、数字、下划线且需保证在商户端不重复
     */
    //private String orderRequestId = SnowflakeIdUtil.nextIdStr();
    private String orderRequestId = UUID.randomUUID().toString();





}
