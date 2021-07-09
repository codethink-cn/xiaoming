package com.chuanwise.xiaoming.core.listener;

import com.chuanwise.xiaoming.api.annotation.EventHandler;
import com.chuanwise.xiaoming.core.event.EventListenerImpl;
import net.mamoe.mirai.event.events.FriendAddEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;

public class CoreListener extends EventListenerImpl {
    @EventHandler
    public void onFriendAddRequest(NewFriendRequestEvent event) {
        if (getXiaomingBot().getConfiguration().isAutoAcceptFriendAddRequest()) {
            event.accept();
        }
    }
}
