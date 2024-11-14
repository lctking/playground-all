
package com.nullwli.itemservice.mq.domain;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * 消息体包装器
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：12306）获取项目资料
 */
@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@RequiredArgsConstructor
public final class MessageWrapper<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息发送 Keys
     */
    @NonNull
    private String keys;

    /**
     * 消息体
     */
    @NonNull
    private T message;

    /**
     * 唯一标识，用于客户端幂等验证
     */
    private String uuid = UUID.randomUUID().toString();

    /**
     * 消息发送时间
     */
    private Long timestamp = System.currentTimeMillis();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageWrapper<?> that = (MessageWrapper<?>) o;
        return Objects.equals(keys, that.keys) && Objects.equals(message, that.message) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keys, message);
    }
}
