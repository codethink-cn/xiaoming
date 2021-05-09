package com.chuanwise.xiaoming.api.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.mamoe.mirai.event.Event;

/**
 * 监听器响应事件
 */
@Data
@AllArgsConstructor
public class ListenerResponseEvent extends XiaomingEvent {
    EventListener listener;
    Event event;
}