package com.chuanwise.xiaoming.api.interactor;
import java.util.Objects;

/**
 * 上下文相关消息等待器
 * @author Chuanwise
 */
public class MessageWaiter {
    long endTime;
    volatile String value;

    public MessageWaiter(long endTime) {
        this.endTime = endTime;
    }

    public void onInput(String value) {
        if (System.currentTimeMillis() < endTime) {
            this.value = value;
            synchronized (this) {
                notify();
            }
        }
    }

    public String getValue() {
        return Objects.nonNull(value) ? value : null;
    }
}
