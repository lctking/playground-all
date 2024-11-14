package com.nullwli.itemservice.dto.req;

import lombok.Data;

@Data
public class ItemCreateReqDTO {
    private String itemName;

    private String comments;

    private int price;

    private int actualPrice;

    private int status;

    private int stock;
}



