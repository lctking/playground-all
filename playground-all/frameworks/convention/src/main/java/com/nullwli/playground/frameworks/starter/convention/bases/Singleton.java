package com.nullwli.playground.frameworks.starter.convention.bases;

import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@NoArgsConstructor
public final class Singleton {
    private static final ConcurrentHashMap<String, Object> SINGLETON_OBJECTS_POOL = new ConcurrentHashMap<>();

    public static <T> T get(String key, Supplier<T>supplier){
        Object result = SINGLETON_OBJECTS_POOL.get(key);
        if(result == null && (result = supplier.get()) != null){
            SINGLETON_OBJECTS_POOL.put(key,result);
        }
        return result == null ? null : (T) result;

    }

    public static <T> T get(String key){
        Object result = SINGLETON_OBJECTS_POOL.get(key);
        return result == null ? null : (T) result;
    }

    public static <T> void put(String key,T val){
        SINGLETON_OBJECTS_POOL.put(key,val);
    }
}
