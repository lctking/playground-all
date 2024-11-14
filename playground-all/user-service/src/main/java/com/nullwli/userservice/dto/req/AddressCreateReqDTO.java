package com.nullwli.userservice.dto.req;

import lombok.Data;

@Data
public class AddressCreateReqDTO {

    private String userId;

    private String purchaserName;

    private String address;

    private String phone;

    private int priority;
}
