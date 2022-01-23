package cn.chuanwise.xiaoming.listener;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public enum ListenerPriority {
    HIGHEST,
    HIGH,
    NORMAL,
    LOW,
    LOWEST;
}