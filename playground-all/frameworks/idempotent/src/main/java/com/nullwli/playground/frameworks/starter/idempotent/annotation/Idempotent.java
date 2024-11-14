package com.nullwli.playground.frameworks.starter.idempotent.annotation;



import com.nullwli.playground.frameworks.starter.idempotent.constant.IdempotentTypeEnum;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {


    IdempotentTypeEnum type() default IdempotentTypeEnum.TOKEN;

    /**
     * 触发幂等失败逻辑时，返回的错误提示信息
     */
    String message() default "您操作太快，请稍后再试";


    String uniqueKeyPrefix() default "";

    long keyTimeout() default 3600L;

    String key() default "";
}
