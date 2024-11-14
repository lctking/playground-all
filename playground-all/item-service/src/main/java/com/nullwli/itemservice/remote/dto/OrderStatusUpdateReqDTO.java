package com.nullwli.itemservice.remote.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderStatusUpdateReqDTO {
    private String orderSn;
    private Integer status;
}
