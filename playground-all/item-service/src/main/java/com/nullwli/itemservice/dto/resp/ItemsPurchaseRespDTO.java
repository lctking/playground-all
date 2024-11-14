package com.nullwli.itemservice.dto.resp;

import com.nullwli.itemservice.dto.domain.ItemsPurchaserDetailDTO;
import com.nullwli.itemservice.strategy.ItemDiscountStrategy;
import lombok.Builder;
import lombok.Data;

import java.util.List;
/**
 *  商品购买详情
 */
@Data
@Builder
public class ItemsPurchaseRespDTO {
    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 下单人信息
     */
    private ItemsPurchaserDetailDTO purchaserDetail;

    /**
     * 下单商品信息
     */
    private List<ItemPurchaseDetailRespDTO> itemOrderDetails;

    /**
     * 总价
     */
    private int totalPrice;


    /**
     * 实际总价
     */
    private int actualTotalPrice;

    /**
     * 实付
     */
    private int actualWithDiscountTotalPrice;

//    /**
//     * 折扣策略
//     */
//    private ItemDiscountStrategy itemDiscountStrategy;


}
