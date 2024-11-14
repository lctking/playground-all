package com.nullwli.itemservice.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_item")
public class ItemDO {
    private Long id;

    private String itemName;

    private String comments;

    private int price;

    private int actualPrice;

    private int status;

    private int stock;
}
