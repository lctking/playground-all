package com.nullwli.playground.frameworks.starter.user.core;

import com.alibaba.ttl.TransmittableThreadLocal;

public final class UserContext {
    private static final ThreadLocal<UserInfo> USER_CONTEXT=new TransmittableThreadLocal<>();

    public static void setUser(UserInfo userInfo){
        USER_CONTEXT.set(userInfo);
    }

    public static String getUserId(){
        UserInfo userInfo=USER_CONTEXT.get();
        if(userInfo==null||userInfo.getUserId()==null){
            return null;
        }

        return userInfo.getUserId();
    }
    public static String getUsername(){
        UserInfo userInfo=USER_CONTEXT.get();
        if(userInfo==null||userInfo.getUsername()==null){
            return null;
        }

        return userInfo.getUsername();
    }

    public static void removeUser() {
        USER_CONTEXT.remove();
    }

    public static UserInfo getUserInfo() {
        return USER_CONTEXT.get();
    }
}
