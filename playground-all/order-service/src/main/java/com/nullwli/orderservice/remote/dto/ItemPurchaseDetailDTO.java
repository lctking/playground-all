package com.nullwli.orderservice.remote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPurchaseDetailDTO {

    private String itemId;

    private String itemName;

    private int amount;

    /**
     * 原价
     */
    private int price;

    /**
     * 优惠后实付价格
     */
    private int actualWithDiscountPrice;


}
