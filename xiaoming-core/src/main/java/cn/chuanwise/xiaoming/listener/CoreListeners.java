package cn.chuanwise.xiaoming.listener;

import cn.chuanwise.xiaoming.annotation.EventListener;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.contact.message.MessageImpl;
import cn.chuanwise.xiaoming.event.InteractorErrorEvent;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.event.SendMessageEvent;
import cn.chuanwise.xiaoming.event.SimpleListeners;
import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

public class CoreListeners extends SimpleListeners {
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

    @EventListener
    public void onSendMessage(SendMessageEvent event) throws InterruptedException, ExecutionException {
        xiaomingBot.getContactManager().readyToSend(event).get();
    }

    @EventListener
    public void onInteractorError(InteractorErrorEvent event) {
        if (event.isReported()) {
            return;
        }

        final InteractorContext context = event.getContext();
        final Throwable throwable = event.getThrowable();

        final XiaomingUser user = context.getUser();
        user.sendError("{lang.internalError}");
        getLogger().error("和用户 " + user.getCompleteName() + " 交互时出现异常", throwable);
        getXiaomingBot().getReportMessageManager().addThrowableMessage(user, throwable);
    }
}