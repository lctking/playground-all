package com.nullwli.userservice.dto.resp;

import lombok.Data;

@Data
public class AddressQueryRespDTO {
    private String purchaserName;

    private String address;

    private String phone;

    private int priority;
}
