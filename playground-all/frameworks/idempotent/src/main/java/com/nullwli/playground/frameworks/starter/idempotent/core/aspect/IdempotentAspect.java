package com.nullwli.playground.frameworks.starter.idempotent.core.aspect;

import com.nullwli.playground.frameworks.starter.idempotent.annotation.Idempotent;
import com.nullwli.playground.frameworks.starter.idempotent.core.IdempotentContext;
import com.nullwli.playground.frameworks.starter.idempotent.core.handler.IdempotentExecuteHandler;
import com.nullwli.playground.frameworks.starter.idempotent.core.handler.IdempotentExecuteHandlerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public final class IdempotentAspect {


    @Around("@annotation(com.nullwli.playground.frameworks.starter.idempotent.annotation.Idempotent)")
    public Object idempotentHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        Idempotent idempotent = getIdempotent(joinPoint);
        IdempotentExecuteHandler instance = IdempotentExecuteHandlerFactory.getInstance(idempotent.type());
        Object resultObj;
        try {
            instance.execute(joinPoint, idempotent);//消费中
            resultObj = joinPoint.proceed();
            instance.postProcessing();//消费完毕
        } catch (Exception ex) {
            /**
             * 触发幂等逻辑时可能有两种情况：
             *    * 1. 消息还在处理，但是不确定是否执行成功，那么需要返回错误，方便 RocketMQ 再次通过重试队列投递
             *    * 2. 消息处理成功了，该消息直接返回成功即可
             */
//            if (ex!=null) {
//                return null;
//            }
            throw ex;
        } catch (Throwable ex) {
            // 客户端消费存在异常，需要删除幂等标识方便下次 RocketMQ 再次通过重试队列投递
            instance.exceptionProcessing();
            throw ex;
        } finally {
            IdempotentContext.clean();
        }
        return resultObj;
    }

    public static Idempotent getIdempotent(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
        return targetMethod.getAnnotation(Idempotent.class);
    }
}
