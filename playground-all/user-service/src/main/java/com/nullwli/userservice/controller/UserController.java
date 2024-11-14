package com.nullwli.userservice.controller;


import com.nullwli.playground.frameworks.starter.convention.result.Result;
import com.nullwli.playground.frameworks.starter.convention.result.Results;
import com.nullwli.playground.frameworks.starter.idempotent.annotation.Idempotent;
import com.nullwli.playground.frameworks.starter.user.core.UserContext;
import com.nullwli.playground.frameworks.starter.user.core.UserInfo;
import com.nullwli.userservice.dto.req.UserLoginReqDTO;
import com.nullwli.userservice.dto.req.UserRegisterReqDTO;
import com.nullwli.userservice.dto.req.UserUpdateReqDTO;
import com.nullwli.userservice.dto.resp.UserLoginRespDTO;
import com.nullwli.userservice.dto.resp.UserRegisterRespDTO;
import com.nullwli.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/user-service/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestPram) throws Exception {
        return Results.success(userService.login(requestPram));
    }

    @GetMapping("/api/user-service/user/check-login")
    public Result<UserLoginRespDTO> login(@RequestParam("identifyToken") String identifyToken) throws Exception {
        return Results.success(userService.checklogin(identifyToken));
    }

    @GetMapping("/api/user-service/user/filter-test")
    public Result<UserInfo> testFilter() {
        return Results.success(UserContext.getUserInfo());
    }

    @Idempotent(//这里只是测试是否幂等，该场景并不需要使用该组件（例如password不能作为去重标准）
            uniqueKeyPrefix = "user-register",
            keyTimeout = -1,
            message = "用户名重复",
            key = "#requestPram.hashForIdempotent()"
    )
    @PostMapping("/api/user-service/user/register")
    @CrossOrigin(origins = "*") // 这里设置为通配符，表示允许所有来源
    public Result<UserRegisterRespDTO> register(@RequestBody UserRegisterReqDTO requestPram) throws Exception {
        return Results.success(userService.register(requestPram));
    }


    @PostMapping("/api/user-service/user/update")
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestPram) throws Exception {
        userService.update(requestPram);
        return Results.success();
    }



}
