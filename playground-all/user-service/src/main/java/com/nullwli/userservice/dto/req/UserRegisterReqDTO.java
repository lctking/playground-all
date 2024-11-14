package com.nullwli.userservice.dto.req;

import lombok.Data;

import java.util.Objects;

import static java.util.Objects.hash;

@Data
public class UserRegisterReqDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;


    private String phone;

    @Override
    public String toString() {
        return "UserRegisterReqDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRegisterReqDTO that = (UserRegisterReqDTO) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return hash(username, password, phone);
    }
    public String hashForIdempotent() {
        return username;
    }
}
