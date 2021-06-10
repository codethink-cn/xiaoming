package com.chuanwise.xiaoming.api.async;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 异步操作结果
 * @author Chuanwise
 */
public class AsyncResult<T> implements Runnable {
    @Getter
    volatile boolean running;

    @Getter
    final Callable<T> callable;

    @Getter
    volatile Exception exception;

    volatile T result = null;

    public AsyncResult(Callable<T> callable) {
        this.callable = callable;
    }

    @Override
    public final void run() {
        running = true;
        try {
            result = callable.call();
        } catch (Exception exception) {
            this.exception = exception;
        } finally {
            running = false;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    public T get(long timeout) throws InterruptedException {
        if (running) {
            synchronized (this) {
                wait(timeout);
            }
        }

        if (running) {
            return null;
        } else {
            return result;
        }
    }

    public T get() throws InterruptedException {
        return get(0);
    }
}
