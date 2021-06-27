package com.chuanwise.xiaoming.api.schedule.async;

public interface AsyncResult<T> extends Runnable {
    void cancel();

    void interrupt();

    T get(long timeout) throws InterruptedException;

    T get() throws InterruptedException;

    boolean isRunning();

    boolean isCancelled();

    boolean isFinished();

    java.util.concurrent.Callable<T> getCallable();

    Exception getException();
}
