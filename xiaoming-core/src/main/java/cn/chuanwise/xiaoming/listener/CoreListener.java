package cn.chuanwise.xiaoming.listener;

import cn.chuanwise.xiaoming.annotation.EventListener;
import cn.chuanwise.xiaoming.event.SimpleListeners;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;

public class CoreListener extends SimpleListeners {
    @EventListener
    public void onFriendAddRequest(NewFriendRequestEvent event) {
        if (getXiaomingBot().getConfiguration().isAutoAcceptFriendAddRequest()) {
            event.accept();
        }
    }

    @EventListener
    public void onGroupInvite(BotInvitedJoinGroupRequestEvent event) {
        if (getXiaomingBot().getConfiguration().isAutoAcceptGroupInvite()) {
            event.accept();
        }
    }
}
