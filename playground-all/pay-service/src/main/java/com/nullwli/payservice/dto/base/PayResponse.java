package com.nullwli.payservice.dto.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class PayResponse {

    /**
     * 调用支付返回信息
     */
    private String body;
}
