package com.nullwli.itemservice.dto.resp;

import lombok.Data;
/**
 *  查询商品信息
 */
@Data
public class ItemQueryRespDTO {
    private String id;

    private String itemName;

    private String comments;

    private int price;

    private int actualPrice;

    private int status;

    private int stock;
}
