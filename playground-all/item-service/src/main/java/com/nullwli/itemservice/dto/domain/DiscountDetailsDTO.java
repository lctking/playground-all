package com.nullwli.itemservice.dto.domain;

import lombok.Data;

@Data
public class DiscountDetailsDTO {

    private int discountValue;

    private String discountType;//百分比，固定值，满减

    private int discountThreshold;

}
