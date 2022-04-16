package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Collections;
import cn.codethink.xiaoming.AbstractBot;
import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.exception.NoSuchFriendException;
import cn.codethink.xiaoming.exception.NoSuchGroupException;
import cn.codethink.xiaoming.exception.NoSuchMemberException;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.MiraiCompoundMessage;
import cn.codethink.xiaoming.message.MiraiMessageChain;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import net.mamoe.mirai.contact.AnonymousMember;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.event.events.BotAvatarChangedEvent;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.BotReloginEvent;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageRecallEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 事件转发器
 *
 * @author Chuanwise
 */
public class EventForwarder
        extends AbstractBotObject
        implements ListenerHost {
    
    private volatile String avatarUrl;
    
    public EventForwarder(Bot bot) {
        super(bot);
        
        avatarUrl = bot.asFriend().getAvatarUrl();
    }
    
    @EventHandler
    public void onBotOnline(BotOnlineEvent event) {
        final cn.codethink.xiaoming.event.Event newEvent = new cn.codethink.xiaoming.event.BotOnlineEvent(bot);
        bot.getEventManager().broadcastEvent(newEvent);
    }
    
    @EventHandler
    public void onBotOffline(BotOfflineEvent event) {
        final cn.codethink.xiaoming.event.Event newEvent = new cn.codethink.xiaoming.event.BotOfflineEvent(bot, event.getReconnect());
        bot.getEventManager().broadcastEvent(newEvent);
    }
    
    @EventHandler
    public void onBotRelogin(BotReloginEvent event) {
        event.cancel();
    }
    
    @EventHandler
    public void onBotAvatarChanged(BotAvatarChangedEvent event) {
        final String previousAvatarUrl = avatarUrl;
        final String currentAvatarUrl = bot.getAvatarUrl();
        this.avatarUrl = currentAvatarUrl;
        
        final cn.codethink.xiaoming.event.Event newEvent = new cn.codethink.xiaoming.event.BotAvatarChangedEvent(
            bot,
            previousAvatarUrl,
            currentAvatarUrl
        );
        bot.getEventManager().broadcastEvent(newEvent);
    }
    
    @EventHandler
    public void onBotNickChanged(BotNickChangedEvent event) {
        final cn.codethink.xiaoming.event.Event newEvent = new BotNameChangedEvent(bot, event.getFrom(), event.getTo());
        bot.getEventManager().broadcastEvent(newEvent);
    }
    
    @EventHandler
    public void onGroupMessageEvent(GroupMessageEvent event) {
        // get group
        final Code groupCode = Code.ofLong(event.getGroup().getId());
        final Group group = bot.getGroupOrFail(groupCode);
    
        final GroupSender sender;
        final net.mamoe.mirai.contact.Member member = event.getSender();
        if (member instanceof NormalMember) {
            final NormalMember normalMember = (NormalMember) member;
            sender = group.getMemberOrFail(Code.ofLong(normalMember.getId()));
        } else if (member instanceof AnonymousMember) {
            final AnonymousMember anonymousMember = (AnonymousMember) member;
            sender = ((MiraiGroup) group).getAnonymous().getAvailable(anonymousMember);
        } else {
            throw new NoSuchElementException("unknown member class: " + member.getClass());
        }
    
        final CompoundMessage compoundMessage = MiraiMessageChain.toCompoundMessage(event.getMessage(), event.getGroup(), (MiraiBot) bot);
        final Event newEvent = new ReceiveGroupMessageEvent(
            sender,
            compoundMessage,
            TimeUnit.SECONDS.toMillis(event.getTime())
        );
        bot.getEventManager().broadcastEvent(newEvent);
    }
    
    @EventHandler
    public void onGroupTempMessageEvent(GroupTempMessageEvent event) {
        final GroupMember member = bot.getGroupOrFail(Code.ofLong(event.getGroup().getId()))
            .getMemberOrFail(Code.ofLong(event.getSender().getId()));
    
        final CompoundMessage compoundMessage = MiraiMessageChain.toCompoundMessage(event.getMessage(), event.getSender(), (MiraiBot) bot);
        final Event newEvent = new ReceiveGroupMemberMessageEvent(
            member,
            compoundMessage,
            TimeUnit.SECONDS.toMillis(event.getTime())
        );
        bot.getEventManager().broadcastEvent(newEvent);
    }
    
    @EventHandler
    public void onFriendMessageEvent(FriendMessageEvent event) {
        final Friend friend = bot.getFriendOrFail(Code.ofLong(event.getFriend().getId()));
        final CompoundMessage compoundMessage = MiraiMessageChain.toCompoundMessage(event.getMessage(), event.getSender(), (MiraiBot) bot);
        
        final Event newEvent = new ReceiveFriendMessageEvent(
            friend,
            compoundMessage,
            TimeUnit.SECONDS.toMillis(event.getTime())
        );
        bot.getEventManager().broadcastEvent(newEvent);
    }
    
//    @EventHandler
//    public void onGroupMessageRecallEvent(MessageRecallEvent.GroupRecall event) {
//        // find a msg
//        final AbstractBot abstractBot = (AbstractBot) this.bot;
//        final Collection<Message> messages = abstractBot.getResourcePool().getMessages().values();
//
//        // find the recalled message
//        final Message message = Collections.firstIf(messages, msg -> {
//            final MiraiCompoundMessage miraiCompoundMessage = (MiraiCompoundMessage) msg;
//            return Arrays.equals(miraiCompoundMessage.getMessageSource().getIds(), event.getMessageIds());
//        });
//
//        // find group
//        final Code groupCode = Code.ofLong(event.getGroup().getId());
//        final Group group = (Group) bot.getMass(groupCode);
//        if (Objects.isNull(group)) {
//            throw new NoSuchGroupException(bot, groupCode);
//        }
//
//        // find operator
//        final GroupMember member;
//        final net.mamoe.mirai.contact.Member operator = event.getOperator();
//        if (Objects.nonNull(operator)) {
//            final Code operatorCode = Code.ofLong(operator.getId());
//            member = group.getMember(operatorCode);
//            if (Objects.isNull(member)) {
//                throw new NoSuchMemberException(group, operatorCode);
//            }
//        } else {
//            member = group.getBotAsMember();
//        }
//
//        bot.getEventManager().broadcastEvent(new GroupMessageRecallEvent(message, member));
//    }
//
//    @EventHandler
//    public void onFriendMessageRecallEvent(MessageRecallEvent.FriendRecall event) {
//        // find a msg
//        final MiraiBot miraiBot = (MiraiBot) this.bot;
//        final Collection<Message> messages = miraiBot.getResourcePool().getMessages().values();
//
//        // find the recalled message
//        final Message message = Collections.firstIf(messages, msg -> {
//            final MiraiCompoundMessage miraiCompoundMessage = (MiraiCompoundMessage) msg;
//            return Arrays.equals(miraiCompoundMessage.getMessageSource().getIds(), event.getMessageIds());
//        });
//
//        final FriendMessageRecallEvent newEvent = new FriendMessageRecallEvent(message);
//        bot.getEventManager().broadcastEvent(newEvent);
//    }
    
    
}
