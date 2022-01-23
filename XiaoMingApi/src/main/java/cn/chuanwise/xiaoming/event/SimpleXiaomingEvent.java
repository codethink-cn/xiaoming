package cn.chuanwise.xiaoming.event;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.object.XiaomingObject;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.event.CancellableEvent;
import net.mamoe.mirai.event.Event;

public class SimpleXiaomingEvent implements XiaomingEvent {
    protected volatile boolean intercept = false;

    @Getter
    @Setter
    protected volatile XiaomingBot xiaomingBot;

    @Override
    public boolean isIntercepted() {
        return intercept;
    }

    @Override
    public void intercept() {
        intercept = true;
    }
}
