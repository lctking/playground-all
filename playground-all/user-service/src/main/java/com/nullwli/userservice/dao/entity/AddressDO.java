package com.nullwli.userservice.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_address")
public class AddressDO {
    private Long id;

    private Long userId;

    private String purchaserName;

    private String address;

    private String phone;

    private int priority;

}
