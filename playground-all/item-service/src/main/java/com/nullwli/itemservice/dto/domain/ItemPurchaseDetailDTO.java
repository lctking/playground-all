package com.nullwli.itemservice.dto.domain;

import lombok.Builder;
import lombok.Data;

/**
 *  订单中单类商品购买详情
 */
@Data
@Builder
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
