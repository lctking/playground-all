package com.nullwli.userservice.dto.resp;

import lombok.Data;

@Data
public class UserRegisterRespDTO {

    /**
     * 用户名
     */
    private String username;


    private String phone;
}
