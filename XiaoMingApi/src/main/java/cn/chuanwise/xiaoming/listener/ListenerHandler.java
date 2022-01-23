package cn.chuanwise.xiaoming.listener;

import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.Data;
import net.mamoe.mirai.event.Event;

@Data
public class ListenerHandler<T extends Event> {
    final ListenerPriority priority;
    final Class<T> eventClass;
    final Listener<T> listener;
    final boolean listenCancelledEvent;
    final Plugin plugin;
}
