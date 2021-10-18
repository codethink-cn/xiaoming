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
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import net.mamoe.mirai.event.events.MessageRecallEvent;
import net.mamoe.mirai.message.data.OnlineMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.util.Map;
import java.util.Objects;

/**
 * @author Chuanwise
 */
@Getter
public class ReceptionistManagerImpl
        extends ModuleObjectImpl
        implements ReceptionistManager, Listeners {
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
        final OnlineMessageSource.Incoming.FromGroup source = event.getSource();
        final Message message = new MessageImpl(xiaomingBot,
                event.getMessage(),
                source.getIds(),
                source.getInternalIds(),
                ((long) event.getTime()) * 1000);

        xiaomingBot.getEventManager().callEventAsync(new MessageEvent(user, message));
    }

    @Override
    @EventListener
    public void onPrivateMessageEvent(FriendMessageEvent event) {
        final Friend friend = event.getFriend();

        final long accountCode = friend.getId();
        final Receptionist receptionist = getReceptionist(accountCode);
        final PrivateXiaomingUser user = receptionist.getPrivateXiaomingUser();

        final OnlineMessageSource.Incoming.FromFriend source = event.getSource();
        final Message message = new MessageImpl(xiaomingBot,
                event.getMessage(),
                source.getIds(),
                source.getInternalIds(),
                ((long) event.getTime()) * 1000);

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
        final OnlineMessageSource.Incoming.FromTemp source = event.getSource();
        final Message message = new MessageImpl(xiaomingBot,
                event.getMessage(),
                source.getIds(),
                source.getInternalIds(),
                ((long) event.getTime()) * 1000);

        xiaomingBot.getEventManager().callEventAsync(new MessageEvent(user, message));
    }

    @EventListener(priority = ListenerPriority.LOW)
    public void onMessageEvent(MessageEvent messageEvent) {
        final Message message = messageEvent.getMessage();
        final XiaomingUser user = messageEvent.getUser();

        // 唤醒正在等待这一条消息的线程
        xiaomingBot.getContactManager().onNextMessageEvent(messageEvent);

        if (Objects.nonNull(user.getInteractorContext())) {
            getLogger().info(user.getCompleteName() + "已有交互上下文，不再启动新的接待任务");
            return;
        }

        xiaomingBot.getScheduler().run(new ReceptionTaskImpl<>(user, message));
    }
}
