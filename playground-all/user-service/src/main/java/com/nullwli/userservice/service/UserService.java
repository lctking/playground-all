package com.nullwli.userservice.service;  
import com.baomidou.mybatisplus.extension.service.IService;
import com.nullwli.userservice.dao.entity.UserDO;
import com.nullwli.userservice.dto.req.UserLoginReqDTO;
import com.nullwli.userservice.dto.req.UserRegisterReqDTO;
import com.nullwli.userservice.dto.req.UserUpdateReqDTO;
import com.nullwli.userservice.dto.resp.UserLoginRespDTO;
import com.nullwli.userservice.dto.resp.UserRegisterRespDTO;


public interface UserService extends IService<UserDO> {
    UserLoginRespDTO login(UserLoginReqDTO requestPram) throws Exception;

    UserLoginRespDTO checklogin(String identifyToken) throws Exception;

    UserRegisterRespDTO register(UserRegisterReqDTO requestPram) throws Exception;

    void update(UserUpdateReqDTO requestPram) throws Exception;
}
