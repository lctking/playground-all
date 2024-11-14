package com.nullwli.orderservice.dto.req;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderStatusUpdateReqDTO {
    private String orderSn;
    private Integer status;
}
