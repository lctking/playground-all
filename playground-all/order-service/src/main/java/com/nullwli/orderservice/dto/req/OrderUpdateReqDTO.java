package com.nullwli.orderservice.dto.req;

import lombok.Data;

import java.util.Date;

@Data
public class OrderUpdateReqDTO {

    private String orderSn;

    private String userId;

    private String addressId;

    private String username;

    private Integer status;

    private Integer payType;

    private Date payTime;

    private Date orderTime;

    private Integer delFlag;
}
