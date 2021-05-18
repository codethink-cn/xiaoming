package com.chuanwise.xiaoming.api.object;

public interface XiaomingThread extends Runnable {
    void stop();

    default void forceStop() {
        stop();
    }
}
