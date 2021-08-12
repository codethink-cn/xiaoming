package cn.chuanwise.xiaoming.event;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.object.XiaomingObject;
import lombok.Data;
import lombok.Getter;
import net.mamoe.mirai.event.Event;

@Data
public class XiaomingEvent implements Event, XiaomingObject {
    boolean intercept = false;
    XiaomingBot xiaomingBot;

    @Override
    public boolean isIntercepted() {
        return intercept;
    }

    @Override
    public void intercept() {
        intercept = true;
    }
}
