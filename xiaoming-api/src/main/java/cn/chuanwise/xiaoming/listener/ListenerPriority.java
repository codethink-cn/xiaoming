package cn.chuanwise.xiaoming.listener;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public enum ListenerPriority {
    HIGHEST(4),
    HIGH(3),
    NORMAL(2),
    LOW(1),
    LOWEST(0);

    int priority;
}