package com.nullwli.playground.frameworks.starter.idempotent.core.handler;

import com.nullwli.playground.frameworks.starter.idempotent.annotation.Idempotent;
import com.nullwli.playground.frameworks.starter.idempotent.core.IdempotentParamWrapper;
import org.aspectj.lang.ProceedingJoinPoint;

public interface IdempotentExecuteHandler {

    /**
     * 幂等处理逻辑
     *
     * @param wrapper 幂等参数包装器
     */
    void handler(IdempotentParamWrapper wrapper) throws Exception;

    /**
     * 执行幂等处理逻辑
     *
     * @param joinPoint  AOP 方法处理
     * @param idempotent 幂等注解
     */
    void execute(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Exception;

    /**
     * 异常流程处理
     */
    default void exceptionProcessing() {

    }

    /**
     * 后置处理
     */
    default void postProcessing() {

    }
}
