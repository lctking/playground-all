
package com.nullwli.gatewayservice.filter;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.nullwli.gatewayservice.config.Config;
import com.nullwli.gatewayservice.utils.JWTUtil;
import com.nullwli.gatewayservice.utils.UserConstant;
import com.nullwli.gatewayservice.utils.UserInfo;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SpringCloud Gateway Token 拦截器
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：12306）获取项目资料
 */
@Component
public class TokenValidateGatewayFilterFactory extends AbstractGatewayFilterFactory<Config> {

    public TokenValidateGatewayFilterFactory() {
        super(Config.class);
    }

    //TODO 编写user注销服务
    /**
     * 注销用户时需要传递 Token
     */
    public static final String DELETION_PATH = "/api/user-service/deletion";

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String requestPath = request.getPath().toString();
            if (isPathInBlackPreList(requestPath, config.getBlackPathPre())) {
                String token = request.getHeaders().getFirst("Authorization");
                // TODO 需要验证 Token 是否有效，有可能用户注销了账户，但是 Token 有效期还未过
                UserInfo userInfo = JWTUtil.parseJwtToken(token);
                if (!validateToken(userInfo)) {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }

                ServerHttpRequest.Builder builder = exchange.getRequest().mutate().headers(httpHeaders -> {
                    httpHeaders.set(UserConstant.USER_ID_KEY, userInfo.getUserId());
                    httpHeaders.set(UserConstant.USER_NAME_KEY, userInfo.getUsername());
                    httpHeaders.set(UserConstant.USER_TOKEN_KEY, token);
//                    if (Objects.equals(requestPath, DELETION_PATH)) {
//                        httpHeaders.set(UserConstant.USER_TOKEN_KEY, token);
//                    }
                });
                return chain.filter(exchange.mutate().request(builder.build()).build());
            }
            return chain.filter(exchange);
        },10);
    }


    private boolean isPathInBlackPreList(String requestPath, List<String> blackPathPre) {
        if (CollectionUtils.isEmpty(blackPathPre)) {
            return false;
        }
        return blackPathPre.stream().anyMatch(requestPath::startsWith);
    }

    private boolean validateToken(UserInfo userInfo) {
        return userInfo != null;
    }
}
