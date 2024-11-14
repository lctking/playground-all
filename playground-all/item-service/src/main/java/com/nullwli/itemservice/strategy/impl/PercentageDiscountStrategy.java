package com.nullwli.itemservice.strategy.impl;

import com.nullwli.itemservice.strategy.ItemDiscountStrategy;


public class PercentageDiscountStrategy implements ItemDiscountStrategy {
    private int percentage = 100;

    public PercentageDiscountStrategy(int percentage) throws Exception {
        if(percentage>=0&&percentage<=100){
            this.percentage = percentage;
        }else{
            throw new Exception("折扣百分比设置错误");
        }
    }

    @Override
    public int applyDiscount(int price) {
        long temp = (long) price * this.percentage;
        temp /= 100L;
        return Math.max((int) temp, 0);
    }
}
