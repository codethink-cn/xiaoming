package cn.chuanwise.xiaoming.core.listener;

import cn.chuanwise.xiaoming.api.annotation.EventHandler;
import cn.chuanwise.xiaoming.core.event.EventListenerImpl;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;

public class CoreListener extends EventListenerImpl {
    @EventHandler
    public void onFriendAddRequest(NewFriendRequestEvent event) {
        if (getXiaomingBot().getConfiguration().isAutoAcceptFriendAddRequest()) {
            event.accept();
        }
    }
}
