package com.chuanwise.xiaoming.api.schedule.task;

import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import com.chuanwise.xiaoming.api.object.XiaomingObject;

import java.util.concurrent.Callable;

public interface ScheduableTask<R> extends XiaomingObject, AsyncResult<R>, Cloneable {
    long getTime();

    long getPeriod();

    void setTime(long time);

    void setPeriod(long period);

    void setCallable(Callable<R> callable);

    Callable<R> getCallable();

    String getDescription();

    boolean isPeriodic();

    public ScheduableTask clone() throws CloneNotSupportedException;
}
