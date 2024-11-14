package com.nullwli.itemservice.dto.domain;

import lombok.Data;

@Data
public class AddressQueryRespDTO {
    private String purchaserName;

    private String address;

    private String phone;

    private int priority;
}
