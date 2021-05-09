package com.chuanwise.xiaoming.api.event;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import lombok.Data;
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
