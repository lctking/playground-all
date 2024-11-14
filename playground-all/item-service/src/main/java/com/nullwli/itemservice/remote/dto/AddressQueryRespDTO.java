package com.nullwli.itemservice.remote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressQueryRespDTO {
    private String purchaserName;

    private String address;

    private String phone;

    private int priority;


}
