
package com.nullwli.gatewayservice.config;

import lombok.Data;

import java.util.List;

/**
 * 过滤器配置
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：12306）获取项目资料
 */
@Data
public class Config {

    /**
     * 黑名单前置路径
     */
    private List<String> blackPathPre;
}
