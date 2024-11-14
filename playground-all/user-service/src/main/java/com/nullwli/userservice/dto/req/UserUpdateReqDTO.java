package com.nullwli.userservice.dto.req;

import lombok.Data;

@Data
public class UserUpdateReqDTO {
    /**
     * id
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;


    private String phone;

}
