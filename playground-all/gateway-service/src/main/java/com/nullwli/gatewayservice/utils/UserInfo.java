package com.nullwli.gatewayservice.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {
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