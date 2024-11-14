package com.nullwli.itemservice.strategy.impl;

import com.nullwli.itemservice.strategy.ItemDiscountStrategy;

public class FixedValueDiscountStrategy implements ItemDiscountStrategy {
    private int discountAmount = 0;

    public FixedValueDiscountStrategy(int discountAmount) throws Exception {
        if(discountAmount<0){
            throw new Exception("折扣值小于零，设置错误！");
        }
        this.discountAmount = discountAmount;
    }


    @Override
    public int applyDiscount(int price) {
        if(price < 0)return 0;
        if(discountAmount < 0)return price;

        int val = price - discountAmount;
        if(val < 0)val = 0;
        return val;
    }
}
