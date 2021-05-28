package com.chuanwise.xiaoming.api.time.task;

import com.chuanwise.xiaoming.api.object.XiaomingObject;

public interface TimeTask extends XiaomingObject, Runnable {
    long getTime();

    long getPeriod();

    boolean isAccess();

    void setTime(long time);

    void setPeriod(long period);

    void setAccess(boolean access);

    String getDescription();

    boolean isPeriodic();
}
