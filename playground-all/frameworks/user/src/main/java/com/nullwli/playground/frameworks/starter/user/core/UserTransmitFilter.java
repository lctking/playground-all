

package com.nullwli.playground.frameworks.starter.user.core;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String userId = httpServletRequest.getHeader(UserConstant.USER_ID_KEY);
        String identifyToken =httpServletRequest.getHeader(UserConstant.USER_TOKEN_KEY);
        if (StringUtils.hasText(userId)) {
            String userName = httpServletRequest.getHeader(UserConstant.USER_NAME_KEY);
            if (StringUtils.hasText(userName)) {
                userName = URLDecoder.decode(userName, UTF_8);
            }
            UserInfo userInfoDTO = UserInfo.builder()
                    .userId(userId)
                    .username(userName)
                    .identifyToken(identifyToken)
                    .build();
            UserContext.setUser(userInfoDTO);
        }
//        else if(StringUtils.hasText(identifyToken)){
//            //UserInfo userInfo = JWTUtil.parseJwtToken(identifyToken);
//            UserInfo userInfo = JSON.parseObject(stringRedisTemplate.opsForValue().get("user:info:identifyToken"+identifyToken),UserInfo.class);
//            if(!Objects.isNull(userInfo)){
//                userInfo.setIdentifyToken(identifyToken);
//                UserContext.setUser(userInfo);
//            }
//        }


        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }
}
