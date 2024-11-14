/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nullwli.playground.frameworks.starter.idempotent.config;

import com.nullwli.playground.frameworks.starter.idempotent.core.IdempotentSpELService;
import com.nullwli.playground.frameworks.starter.idempotent.core.aspect.IdempotentAspect;
import com.nullwli.playground.frameworks.starter.idempotent.core.handler.IdempotentSpELByRestAPIExecuteHandler;

import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class IdempotentAutoConfiguration {


    /**
     * 幂等切面
     */
    @Bean
    public IdempotentAspect idempotentAspect() {
        return new IdempotentAspect();
    }




    /**
     * SpEL 方式幂等实现，基于 RestAPI 场景
     */
    @Bean
    public IdempotentSpELService idempotentSpELByRestAPIExecuteHandler(RedissonClient redissonClient, StringRedisTemplate stringRedisTemplate) {
        return new IdempotentSpELByRestAPIExecuteHandler(redissonClient,stringRedisTemplate);
    }


}