package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.toolkit.sized.SizedResidentConcurrentHashMap;
import cn.chuanwise.util.*;
import cn.chuanwise.xiaoming.annotation.EventListener;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.contact.message.MessageImpl;
import cn.chuanwise.xiaoming.event.Listeners;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.listener.ListenerPriority;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.MemberXiaomingUser;
import cn.chuanwise.xiaoming.user.PrivateXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import org.slf4j.Logger;

import java.beans.Transient;
import java.util.Map;
import java.util.Objects;

/**
 * @author Chuanwise
 */
@Slf4j
@Getter
public class ReceptionistManagerImpl extends ModuleObjectImpl implements ReceptionistManager, Listeners {
    @Override
    @Transient
    public Logger getLogger() {
        return log;
    }

    public ReceptionistManagerImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
        this.receptionists = new SizedResidentConcurrentHashMap<>(xiaomingBot.getConfiguration().getMaxReceptionistQuantity());
    }

    /** 用户接待员记录器 */
    final Map<Long, Receptionist> receptionists;

    @Override
    public Receptionist getReceptionist(long code) {
        return MapUtil.getOrPutSupply(receptionists, code, () -> new ReceptionistImpl(getXiaomingBot(), code));
    }

    @Override
    @EventListener
    public void onGroupMessageEvent(GroupMessageEvent event) {
        final Group group = event.getGroup();
        final Member member = event.getSender();

        final long accountCode = member.getId();
        final Receptionist receptionist = getReceptionist(accountCode);

        final long groupCode = group.getId();
        final GroupXiaomingUser user = receptionist.getGroupXiaomingUser(groupCode);
        final Message message = new MessageImpl(xiaomingBot, event.getMessage(), event.getTime());

        xiaomingBot.getEventManager().callEventAsync(new MessageEvent(user, message));
    }

    @Override
    @EventListener
    public void onPrivateMessageEvent(FriendMessageEvent event) {
        final Friend friend = event.getFriend();

        final long accountCode = friend.getId();
        final Receptionist receptionist = getReceptionist(accountCode);
        final PrivateXiaomingUser user = receptionist.getPrivateXiaomingUser();
        final Message message = new MessageImpl(xiaomingBot, event.getMessage(), event.getTime());

        xiaomingBot.getEventManager().callEventAsync(new MessageEvent(user, message));
    }

    @Override
    @EventListener
    public void onMemberMessageEvent(GroupTempMessageEvent event) {
        final Group group = event.getGroup();
        final NormalMember member = event.getSender();

        final long accountCode = member.getId();
        final Receptionist receptionist = getReceptionist(accountCode);

        final long groupCode = group.getId();
        final MemberXiaomingUser user = receptionist.getMemberXiaomingUser(groupCode);
        final Message message = new MessageImpl(xiaomingBot, event.getMessage(), event.getTime());

        xiaomingBot.getEventManager().callEventAsync(new MessageEvent(user, message));
    }

    @EventListener(priority = ListenerPriority.LOWEST)
    public void onMessageEvent(MessageEvent messageEvent) {
        final Message message = messageEvent.getMessage();
        final XiaomingUser user = messageEvent.getUser();
        final String serializedMessage = message.serialize();

        // 唤醒正在等待这一条消息的线程
        xiaomingBot.getContactManager().onNextMessageEvent(messageEvent);

        // 检查明确调用
        if (!messageEvent.isInteractable()) {
            return;
        }

        if (Objects.nonNull(user.getInteractorContext())) {
            getLogger().info(user.getCompleteName() + "已有交互上下文，不再启动新的接待任务");
            return;
        }

        xiaomingBot.getScheduler().run(new ReceptionTaskImpl<>(user, message));
    }
}
