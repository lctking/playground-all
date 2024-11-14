package com.nullwli.orderservice.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import java.util.Date;
@Data
@TableName("t_order")
@Builder
public class OrderDO {
    private Long id;

    private String orderSn;

    private Long userId;

    private Long addressId;

    private String username;

    private Integer status;

    private Integer payType;

    private Date payTime;

    private Date orderTime;
}
