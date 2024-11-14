
package com.nullwli.playground.frameworks.starter.user.config;

import com.nullwli.playground.frameworks.starter.user.core.UserTransmitFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
//com.nullwli.playground.frameworks.starter.user.config.UserAutoConfiguration.java

/**
 * 用户配置自动装配
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：12306）获取项目资料
 */
@ConditionalOnWebApplication //在网络应用中才被加载为bean，但是不便于测试，因此加上@Component注解

@RequiredArgsConstructor

public class UserAutoConfiguration {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 用户信息传递过滤器
     */
    @Bean
    public FilterRegistrationBean<UserTransmitFilter> globalUserTransmitFilter() {
        FilterRegistrationBean<UserTransmitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserTransmitFilter(stringRedisTemplate));
        registration.addUrlPatterns("/*");
        registration.setOrder(100);
        return registration;
    }
}
