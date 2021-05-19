package com.chuanwise.xiaoming.core.event;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.event.EventListener;
import com.chuanwise.xiaoming.api.event.EventManager;
import com.chuanwise.xiaoming.api.event.ListenerResponseEvent;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import lombok.Getter;
import net.mamoe.mirai.event.Event;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 消息处理函数管理器
 */
@Getter
public class EventManagerImpl extends HostObjectImpl implements EventManager {
    Set<EventListener> coreListeners = new CopyOnWriteArraySet<>();

    Map<XiaomingPlugin, Set<EventListener>> pluginListeners = new ConcurrentHashMap<>();

    public EventManagerImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    @Override
    public void denyCoreRegister() {
        coreListeners = Collections.unmodifiableSet(coreListeners);
    }
}