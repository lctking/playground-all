package com.nullwli.orderservice.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderCreateRespDTO {
    private String orderSn;
}
