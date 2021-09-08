package cn.chuanwise.xiaoming.listener;

import cn.chuanwise.xiaoming.annotation.EventListener;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.event.SimpleListeners;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.MessageRecallEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.message.code.MiraiCode;

import java.util.Optional;

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

    @EventListener
    public void onRecall(MessageRecallEvent.GroupRecall recall) {
        final Group group = recall.getGroup();
        final Optional<Message> optionalRecallMessage = xiaomingBot.getContactManager()
                .getRecentMessageEvents()
                .stream()
                .filter(messageEvent -> messageEvent.getUser() instanceof GroupXiaomingUser && messageEvent.getMessage().getTime() == recall.getMessageTime())
                .findFirst()
                .map(MessageEvent::getMessage);
        final String miraiCode;
        miraiCode = optionalRecallMessage
                .map(message -> recall.getOperator().getId() + " 撤回了一条 " + recall.getAuthorId() + " 的消息：" + message.serialize())
                .orElseGet(() -> recall.getOperator().getId() + " 撤回了一条 " + recall.getAuthorId() + " 的消息，但是时间找不到");
        group.sendMessage(MiraiCode.deserializeMiraiCode(miraiCode));
    }
}
