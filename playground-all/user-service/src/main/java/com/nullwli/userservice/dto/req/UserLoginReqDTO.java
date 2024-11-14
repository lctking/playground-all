package com.nullwli.userservice.dto.req;

import lombok.Data;

@Data
public class UserLoginReqDTO {

    /**
     * 用户名或手机号
     */
    private String usernameOrPhone;

    /**
     * 密码
     */
    private String password;

}
