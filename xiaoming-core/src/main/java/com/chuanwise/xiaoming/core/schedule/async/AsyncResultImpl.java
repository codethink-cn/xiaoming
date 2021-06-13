package com.chuanwise.xiaoming.core.schedule.async;

import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
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

    @Getter
    volatile boolean finished = false;

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
        if (finished) {
            throw new XiaomingRuntimeException("已经结束的任务无法重新启动");
        }

        try {
            if (!cancelled) {
                if (Objects.nonNull(callable)) {
                    result = callable.call();
                } else {
                    result = execute();
                }
            }
        } catch (Exception exception) {
            this.exception = exception;
        } finally {
            running = false;
            finished = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    @Override
    public final T get(long timeout) throws InterruptedException {
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
    public final T get() throws InterruptedException {
        return get(0);
    }

    public T execute() {
        return null;
    }

    protected void setFinished(boolean finished) {
        this.finished = finished;
    }
}
