package cn.chuanwise.xiaoming.event;

import net.mamoe.mirai.event.Event;

public class XiaomingEvent implements Event {
    boolean intercept = false;

    @Override
    public boolean isIntercepted() {
        return intercept;
    }

    @Override
    public void intercept() {
        intercept = true;
    }
}
