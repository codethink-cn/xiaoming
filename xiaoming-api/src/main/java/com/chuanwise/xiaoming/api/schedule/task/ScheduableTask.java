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

    String getDescription();

    void setDescription(String description);

    boolean isPeriodic();

    default boolean isTimeout(long from) {
        return from > getTime();
    }

    default boolean isTimeout() {
        return System.currentTimeMillis() >= getTime();
    }

    default long getDelay() {
        return getTime() - System.currentTimeMillis();
    }

    ScheduableTask<R> getSon();

    void setSon(ScheduableTask<R> son);

    ScheduableTask<R> getFather();

    void setFather(ScheduableTask<R> father);

    ScheduableTask<R> clone() throws CloneNotSupportedException;
}
