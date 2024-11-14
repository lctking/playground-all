package com.nullwli.orderservice.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@TableName("t_order_detail")
@Builder
public class OrderDetailDO {
    private long id;

    private String orderSn;

    private long itemId;

    private String itemName;

    private int itemAmount;

    private int originPrice;

    private int amountPaid;

    private int status;


}
