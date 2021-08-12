package cn.chuanwise.xiaoming.listener;

import cn.chuanwise.xiaoming.annotation.EventHandler;
import cn.chuanwise.xiaoming.event.EventListenerImpl;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;

public class CoreListener extends EventListenerImpl {
    @EventHandler
    public void onFriendAddRequest(NewFriendRequestEvent event) {
        if (getXiaomingBot().getConfiguration().isAutoAcceptFriendAddRequest()) {
            event.accept();
        }
    }

    @EventHandler
    public void onGroupInvite(BotInvitedJoinGroupRequestEvent event) {
        if (getXiaomingBot().getConfiguration().isAutoAcceptGroupInvite()) {
            event.accept();
        }
    }
}
