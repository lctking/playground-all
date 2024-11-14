package com.nullwli.orderservice.remote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPurchaseDetailReqDTO {
    private String itemId;

    //商品数量
    private int amount;
}
