package com.nullwli.userservice.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nullwli.playground.frameworks.starter.user.core.UserInfo;
import com.nullwli.playground.frameworks.starter.user.utils.JWTUtil;
import com.nullwli.userservice.dao.entity.UserDO;
import com.nullwli.userservice.dto.req.UserRegisterReqDTO;
import com.nullwli.userservice.dto.req.UserUpdateReqDTO;
import com.nullwli.userservice.dto.resp.UserRegisterRespDTO;
import com.nullwli.userservice.dao.mapper.UserMapper;
import com.nullwli.userservice.dto.req.UserLoginReqDTO;
import com.nullwli.userservice.dto.resp.UserLoginRespDTO;
import com.nullwli.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {


    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestPram) throws Exception {
        UserDO userDo =null;
        LambdaQueryWrapper<UserDO>usernameWrapper= Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername,requestPram.getUsernameOrPhone());

        userDo = userMapper.selectOne(usernameWrapper);
        if(Objects.isNull(userDo)){
            LambdaQueryWrapper<UserDO>phoneWrapper= Wrappers.lambdaQuery(UserDO.class)
                    .eq(UserDO::getPhone,requestPram.getUsernameOrPhone());

            userDo = userMapper.selectOne(phoneWrapper);
        }

        if(Objects.isNull(userDo)){
            //
            throw new Exception("用户信息为空");
        }

        if(!Objects.equals(DigestUtils.sha256Hex(requestPram.getPassword()), userDo.getPassword())){
            throw new Exception("密码错误");
        }
        UserLoginRespDTO userLoginRespDTO=new UserLoginRespDTO();
        UserInfo userInfo=new UserInfo();
        BeanUtils.copyProperties(userDo,userLoginRespDTO);
        userLoginRespDTO.setUserId(String.valueOf(userDo.getId()));
        BeanUtils.copyProperties(userDo,userInfo);
        userInfo.setUserId(String.valueOf(userDo.getId()));

        //log.debug("userInfo="+userInfo);

        String identifyToken= JWTUtil.generateAccessToken(userInfo);
        userLoginRespDTO.setIdentifyToken(identifyToken);

        stringRedisTemplate.opsForValue().set("user:info:identifyToken"+identifyToken, JSON.toJSONString(userInfo),30, TimeUnit.MINUTES);
        //log.debug("decode-userInfo="+JWTUtil.parseJwtToken(identifyToken));

        return userLoginRespDTO;
    }

    @Override
    public UserLoginRespDTO checklogin(String identifyToken) throws Exception {
        //UserInfo userInfo = JWTUtil.parseJwtToken(identifyToken);


        UserInfo userInfo = JSON.parseObject(stringRedisTemplate.opsForValue().get("user:info:identifyToken"+identifyToken),UserInfo.class);

        if(Objects.isNull(userInfo)){
            throw new Exception("用户信息解析失败");
        }
        //将userInfo信息存放入userContext
        //UserContext.setUser(userInfo);

        UserLoginRespDTO userLoginRespDTO=new UserLoginRespDTO();

        userInfo.setIdentifyToken(identifyToken);

        BeanUtils.copyProperties(userInfo,userLoginRespDTO);
        return userLoginRespDTO;

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
//    @Idempotent(//这里只是测试是否幂等，该场景并不需要使用该组件（例如password不能作为去重标准）
//            uniqueKeyPrefix = "user-register",
//            keyTimeout = -1,
//            message = "用户名重复",
//            key = "#requestPram.hashForIdempotent()"
//    )
    public UserRegisterRespDTO register(UserRegisterReqDTO requestPram) throws Exception {

        RLock lock = redissonClient.getLock("user:register:lock" + requestPram.getUsername());
        if(!lock.tryLock()){
            throw new Exception("username重复");
        }
        try{
            try{
                requestPram.setPassword(DigestUtils.sha256Hex(requestPram.getPassword()));
                UserDO userDo =new UserDO();
                BeanUtils.copyProperties(requestPram, userDo);
                int insert = userMapper.insert(userDo);
                if(insert<1){
                    throw new DuplicateKeyException("");
                }
            }catch (DuplicateKeyException dke){
                throw new DuplicateKeyException("username "+requestPram.getUsername()+"重复");
            }
        }finally {
            lock.unlock();
        }
        UserRegisterRespDTO userRegisterRespDTO=new UserRegisterRespDTO();
        BeanUtils.copyProperties(requestPram,userRegisterRespDTO);
        return userRegisterRespDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(UserUpdateReqDTO requestPram) throws Exception {
        UserDO userDo =new UserDO();
        BeanUtils.copyProperties(requestPram, userDo);
        userDo.setPassword(DigestUtils.sha256Hex(requestPram.getPassword()));
        userDo.setId(Long.valueOf(requestPram.getUserId()));
        LambdaUpdateWrapper<UserDO> updateWrapper=Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getId,Long.valueOf(requestPram.getUserId()))
                .eq(UserDO::getUsername,requestPram.getUsername())
                .eq(UserDO::getPassword,DigestUtils.sha256Hex(requestPram.getPassword()));
        try{
            userMapper.update(userDo,updateWrapper);
        }catch (Exception e){
            throw new Exception("用户信息更新失败");
        }


    }


}
