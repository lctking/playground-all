package com.nullwli.playground.frameworks.starter.idempotent.core.handler;


import com.nullwli.playground.frameworks.starter.idempotent.constant.IdempotentTypeEnum;

import static cn.hutool.extra.spring.SpringUtil.getBean;

public final class IdempotentExecuteHandlerFactory {

    public static IdempotentExecuteHandler getInstance(IdempotentTypeEnum type){
        IdempotentExecuteHandler result=null;
        switch (type){
            case TOKEN -> result=getBean(IdempotentSpELByRestAPIExecuteHandler.class);
            default -> {}
        }
        return result;
    }
}
