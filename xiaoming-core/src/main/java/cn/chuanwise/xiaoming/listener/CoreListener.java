package cn.chuanwise.xiaoming.listener;

import cn.chuanwise.xiaoming.annotation.EventHandler;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.event.Listeners;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;

public class CoreListener extends ModuleObjectImpl implements Listeners {
    public CoreListener(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

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
