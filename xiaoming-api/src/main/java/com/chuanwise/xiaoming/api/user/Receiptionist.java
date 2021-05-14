package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.object.HostObject;

import java.util.concurrent.TimeUnit;

public interface Receiptionist extends HostObject, Runnable {
    /**
     * 招待周期
     */
    long RECEIPT_PERIOD = TimeUnit.MINUTES.toMillis(1);
    /**
     * 多长时间不招待就退出
     */
    int NO_RECEIPT_TIME = 2;

    void receipt();

    XiaomingUser getUser();

    int getRecentFreeTime();

    boolean isReceipting();

    void stop();

    boolean isRunning();
}
