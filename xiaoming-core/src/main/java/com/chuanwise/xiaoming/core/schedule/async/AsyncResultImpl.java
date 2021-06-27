package com.chuanwise.xiaoming.core.schedule.async;

import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 异步操作结果
 * @author Chuanwise
 */
@NoArgsConstructor
public class AsyncResultImpl<T> implements AsyncResult<T> {
    @Getter
    volatile AtomicBoolean running = new AtomicBoolean(false);

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Getter
    volatile AtomicBoolean cancelled = new AtomicBoolean(false);

    @Override
    public boolean isCancelled() {
        return cancelled.get();
    }

    @Getter
    volatile AtomicBoolean finished = new AtomicBoolean(false);

    @Override
    public boolean isFinished() {
        return finished.get();
    }

    @Override
    public void cancel() {
        if (isRunning()) {
            interrupt();
        } else {
            cancelled.set(true);
        }
    }

    @Override
    public void interrupt() {
        if (Objects.nonNull(thread)) {
            thread.interrupt();
        }
    }

    @Getter
    @Setter
    Callable<T> callable;

    @Getter
    volatile Exception exception;

    volatile T result = null;

    volatile Thread thread;

    public AsyncResultImpl(Callable<T> callable) {
        this.callable = callable;
    }

    @Override
    public final void run() {
        running.set(true);
        if (isFinished()) {
            LoggerFactory.getLogger(getClass()).warn("重新启动已经结束的任务");
            finished.set(false);
        }
        thread = Thread.currentThread();

        try {
            if (!isCancelled()) {
                if (Objects.nonNull(callable)) {
                    result = callable.call();
                } else {
                    result = execute();
                }
            }
        } catch (InterruptedException exception) {
            running.set(false);
            result = null;
        } catch (Exception exception) {
            this.exception = exception;
        } finally {
            running.set(false);
            finished.set(true);
            thread = null;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    @Override
    public final T get(long timeout) throws InterruptedException {
        while (!isFinished()) {
            synchronized (this) {
                if (!isFinished()) {
                    wait(timeout);
                }
            }
        }
        return isFinished() ? result : null;
    }

    @Override
    public final T get() throws InterruptedException {
        return get(0);
    }

    public T execute() throws Exception {
        return null;
    }

    protected void setFinished(boolean finished) {
        this.finished.set(finished);
    }
}
