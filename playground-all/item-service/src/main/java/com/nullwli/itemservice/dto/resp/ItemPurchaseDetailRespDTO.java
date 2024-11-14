package com.nullwli.itemservice.dto.resp;

import lombok.Builder;
import lombok.Data;

/**
 *  订单中单类商品购买详情
 */
@Data
@Builder
public class ItemPurchaseDetailRespDTO {

    private String itemName;

    private int amount;

    /**
     * 原价
     */
    private int price;

    /**
     * 现价
     */
    private int actualPrice;

    /**
     * 优惠后实付价格
     */
    private int actualWithDiscountPrice;

}
