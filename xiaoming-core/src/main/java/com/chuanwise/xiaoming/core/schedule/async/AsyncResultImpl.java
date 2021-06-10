package com.chuanwise.xiaoming.core.schedule.async;

import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.Callable;

/**
 * 异步操作结果
 * @author Chuanwise
 */
@NoArgsConstructor
public class AsyncResultImpl<T> implements AsyncResult<T> {
    @Getter
    volatile boolean running;

    @Getter
    volatile boolean cancelled = false;

    @Override
    public void cancel() {
        cancelled = true;
    }

    @Getter
    @Setter
    Callable<T> callable;

    @Getter
    volatile Exception exception;

    volatile T result = null;

    public AsyncResultImpl(Callable<T> callable) {
        this.callable = callable;
    }

    @Override
    public final void run() {
        running = true;
        try {
            if (!cancelled) {
                result = callable.call();
            }
        } catch (Exception exception) {
            this.exception = exception;
        } finally {
            running = false;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    @Override
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

    @Override
    public T get() throws InterruptedException {
        return get(0);
    }
}
