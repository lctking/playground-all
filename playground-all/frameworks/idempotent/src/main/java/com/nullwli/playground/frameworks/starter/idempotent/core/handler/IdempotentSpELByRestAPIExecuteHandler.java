
package com.nullwli.playground.frameworks.starter.idempotent.core.handler;

import com.nullwli.playground.frameworks.starter.idempotent.annotation.Idempotent;
import com.nullwli.playground.frameworks.starter.idempotent.core.IdempotentParamWrapper;
import com.nullwli.playground.frameworks.starter.idempotent.core.IdempotentSpELService;
import com.nullwli.playground.frameworks.starter.idempotent.core.aspect.IdempotentAspect;
import com.nullwli.playground.frameworks.starter.idempotent.utils.SpELUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
public final class IdempotentSpELByRestAPIExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentSpELService {

    private final RedissonClient redissonClient;

    private final static String LOCK = "lock:spEL:restAPI";
    private final StringRedisTemplate stringRedisTemplate;

    @SneakyThrows
    @Override
    protected IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint) {
        Idempotent idempotent = IdempotentAspect.getIdempotent(joinPoint);
        String key = (String) SpELUtil.parseKey(idempotent.key(), ((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getArgs());
        //String key=idempotent.uniqueKeyPrefix()+((MethodSignature) joinPoint.getSignature()).getMethod()+ Arrays.toString(joinPoint.getArgs());
        return IdempotentParamWrapper.builder().lockKey(key).joinPoint(joinPoint).build();
    }

    @Override
    public void handler(IdempotentParamWrapper wrapper) throws Exception {
        String uniqueKey = wrapper.getIdempotent().uniqueKeyPrefix()+":" + wrapper.getLockKey();
//        RLock lock = redissonClient.getLock(uniqueKey);
//        if (!lock.tryLock()) {
//            throw new Exception(wrapper.getIdempotent().message());
//        }
////        IdempotentContext.put(LOCK, lock);
//        try{
            long timeOut=wrapper.getIdempotent().keyTimeout();
            Boolean setIfAbsent=false;
            if(timeOut<0){
                setIfAbsent = stringRedisTemplate.opsForValue().setIfAbsent(uniqueKey, "0");
            }else{
                setIfAbsent = stringRedisTemplate.opsForValue().setIfAbsent(uniqueKey, "0", wrapper.getIdempotent().keyTimeout(), TimeUnit.SECONDS);
            }
            if(setIfAbsent==null||!setIfAbsent){
                throw new Exception(wrapper.getIdempotent().message());
            }
//        }finally {
//            lock.unlock();
//        }


    }

//    @Override
//    public void postProcessing() {
//        RLock lock = null;
//        try {
//            lock = (RLock) IdempotentContext.getKey(LOCK);
//        } finally {
//            if (lock != null) {
//                lock.unlock();
//            }
//        }
//    }
//
//    @Override
//    public void exceptionProcessing() {
//        RLock lock = null;
//        try {
//            lock = (RLock) IdempotentContext.getKey(LOCK);
//        } finally {
//            if (lock != null) {
//                lock.unlock();
//            }
//        }
//    }
}
