package com.nullwli.payservice.remote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
