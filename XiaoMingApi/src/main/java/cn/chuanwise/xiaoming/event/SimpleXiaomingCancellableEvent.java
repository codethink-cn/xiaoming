package cn.chuanwise.xiaoming.event;

public class SimpleXiaomingCancellableEvent extends SimpleXiaomingEvent implements XiaomingCancellableEvent {
    protected volatile boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public synchronized void cancel() {
        cancelled = true;
    }
}
