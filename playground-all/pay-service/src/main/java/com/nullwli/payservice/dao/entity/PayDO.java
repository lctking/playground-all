
package com.nullwli.payservice.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nullwli.playground.frameworks.starter.convention.bases.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 支付实体
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：12306）获取项目资料
 */

@Data
@TableName("t_pay")
public class PayDO {

    /**
     * id
     */
    private Long id;

    /**
     * 支付流水号
     */
    private String paySn;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 商户订单号
     */
    private String outOrderSn;

    /**
     * 支付渠道
     */
    private String channel;

    /**
     * 支付环境
     */
    private String tradeType;

    /**
     * 订单标题
     */
    private String subject;

    /**
     * 交易凭证号
     */
    private String tradeNo;

    /**
     * 商户订单号
     * 由商家自定义，64个字符以内，仅支持字母、数字、下划线且需保证在商户端不重复
     */
    private String orderRequestId;

    /**
     * 交易总金额
     */
    private Integer totalAmount;

    /**
     * 付款时间
     */
    private Date gmtPayment;

    /**
     * 支付金额
     */
    private Integer payAmount;

    /**
     * 支付状态
     */
    private String status;
}
