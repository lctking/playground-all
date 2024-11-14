package com.nullwli.itemservice.strategy.impl;

import com.nullwli.itemservice.strategy.ItemDiscountStrategy;

public class ThresholdDiscountStrategy implements ItemDiscountStrategy {
    private int threshold = -1;
    private int discountAmount = -1;

    public ThresholdDiscountStrategy(int threshold, int discountAmount) throws Exception {
        if(threshold < 0 || discountAmount < 0){
            throw new Exception("满减折扣设置错误");
        }
        this.threshold = threshold;
        this.discountAmount = discountAmount;
    }

    @Override
    public int applyDiscount(int price) throws Exception {
        if(this.threshold < 0 || this.discountAmount < 0){
            throw new Exception("满减折扣设置错误");
        }
        if (price >= threshold) {
            return price - discountAmount;
        }
        return price; // 不满足条件，价格不变
    }
}
