package cn.chuanwise.xiaoming.event;

import cn.chuanwise.xiaoming.object.XiaomingObject;
import net.mamoe.mirai.event.Event;

public interface XiaomingEvent extends Event, XiaomingObject {
    default void onCall() {}
}