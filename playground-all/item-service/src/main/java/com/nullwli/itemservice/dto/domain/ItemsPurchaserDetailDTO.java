package com.nullwli.itemservice.dto.domain;

import lombok.Data;
/**
 *  下单人信息
 */
@Data
public class ItemsPurchaserDetailDTO {

    /**
     *  下单人名称
     */
    private String purchaserName;
    /**
     * 送货地址
     */
    private String address;

    /**
     * 手机号
     */
    private String phone;
}

