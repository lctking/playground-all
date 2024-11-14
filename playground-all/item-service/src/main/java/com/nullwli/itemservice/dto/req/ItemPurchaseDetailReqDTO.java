package com.nullwli.itemservice.dto.req;

import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
