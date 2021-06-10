package com.chuanwise.xiaoming.api.time.task;

import com.chuanwise.xiaoming.api.object.XiaomingObject;


public interface TimeTask extends XiaomingObject, Runnable, Cloneable {
    long getTime();

    long getPeriod();

    boolean isSuccess();

    void setTime(long time);

    void setPeriod(long period);

    void setSuccess(boolean success);

    String getDescription();

    boolean isPeriodic();

    public TimeTask clone() throws CloneNotSupportedException;
}
