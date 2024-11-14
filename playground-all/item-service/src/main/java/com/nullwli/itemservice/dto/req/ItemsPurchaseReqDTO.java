package com.nullwli.itemservice.dto.req;

import com.nullwli.itemservice.dto.domain.DiscountDetailsDTO;
import lombok.Data;

import java.util.List;

/**
 * 购物请求，包含商品信息（商品id,数量）数组，下单人信息（地址+名字+手机号），
 */
@Data
public class ItemsPurchaseReqDTO {

    private List<ItemPurchaseDetailReqDTO> itemsDetails;

    private String purchaserAddressId;


    private DiscountDetailsDTO discountDetailsDTO;



}
