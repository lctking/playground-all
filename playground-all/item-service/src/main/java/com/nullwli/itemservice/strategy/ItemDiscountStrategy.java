package com.nullwli.itemservice.strategy;

public interface ItemDiscountStrategy {
    int applyDiscount(int price) throws Exception;
}
