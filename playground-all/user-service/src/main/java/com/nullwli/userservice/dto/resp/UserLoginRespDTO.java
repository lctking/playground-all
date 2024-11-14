package com.nullwli.userservice.dto.resp;

import lombok.Data;

@Data
public class UserLoginRespDTO {
    /**
     * id
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    private String identifyToken;
}
