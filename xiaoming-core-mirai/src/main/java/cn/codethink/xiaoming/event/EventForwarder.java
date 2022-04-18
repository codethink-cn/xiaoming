package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.util.Mirais;
import lombok.Data;
import net.mamoe.mirai.contact.AnonymousMember;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.BotAvatarChangedEvent;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.MessageRecallEvent;
import net.mamoe.mirai.event.events.*;

import java.util.*;
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
    
    ///////////////////////////////////////////////////////////////////////////
    // bot event
    ///////////////////////////////////////////////////////////////////////////
    
    @EventHandler
    public void onBotOnline(BotOnlineEvent event) {
        final cn.codethink.xiaoming.event.Event newEvent = new BotOnlineEventImpl(bot);
        bot.getEventManager().broadcastEvent(newEvent);
    }
    
    @EventHandler
    public void onBotOffline(BotOfflineEvent event) {
        final cn.codethink.xiaoming.event.Event newEvent = new BotOfflineEventImpl(bot, event.getReconnect());
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
        
        final cn.codethink.xiaoming.event.Event newEvent = new BotAvatarChangedEventImpl(
            bot,
            previousAvatarUrl,
            currentAvatarUrl
        );
        bot.getEventManager().broadcastEvent(newEvent);
    }
    
    @EventHandler
    public void onBotNickChanged(BotNickChangedEvent event) {
        final cn.codethink.xiaoming.event.Event newEvent = new BotNameChangedEventImpl(bot, event.getFrom(), event.getTo());
        bot.getEventManager().broadcastEvent(newEvent);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // message event
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * 缓存消息。
     * <p>
     * 每收到或发送一条消息，都将增加一条消息缓存。由于缓存消息使用的是虚哈希表 {@link WeakHashMap}，将在内存不足时
     * 自动释放，不需担心内存问题。
     * <p>
     * 缓存消息的主要用途是在消息撤回时寻找被撤回的消息。
     */
    private final Map<IntArray, CompoundMessage> messageCache = new WeakHashMap<>();
    
    /**
     * 专门用来重写和 int[] 可以相互比较的 IntArray。
     * 是为了解决 int[] 作为键无法查找的问题。
     */
    @Data
    private static class IntArray {
        
        private final int[] value;
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
    
        final CompoundMessage compoundMessage = Mirais.toXiaoMing(event.getMessage(), Collections.singletonMap(Property.CONTACT, group));
        final Event newEvent = new ReceiveGroupMessageEventImpl(
            sender,
            compoundMessage,
            TimeUnit.SECONDS.toMillis(event.getTime())
        );
        
        // cache message
        messageCache.put(new IntArray(event.getSource().getIds()), compoundMessage);
        
        bot.getEventManager().broadcastEvent(newEvent);
    }
    
    @EventHandler
    public void onGroupTempMessageEvent(GroupTempMessageEvent event) {
        final GroupMember member = bot.getGroupOrFail(Code.ofLong(event.getGroup().getId()))
            .getMemberOrFail(Code.ofLong(event.getSender().getId()));
    
        final CompoundMessage compoundMessage = Mirais.toXiaoMing(event.getMessage(), Collections.singletonMap(Property.CONTACT, member));
        final Event newEvent = new ReceiveGroupMemberMessageEventImpl(
            member,
            compoundMessage,
            TimeUnit.SECONDS.toMillis(event.getTime())
        );
        
        // cache message
        messageCache.put(new IntArray(event.getSource().getIds()), compoundMessage);
    
        bot.getEventManager().broadcastEvent(newEvent);
    }
    
    @EventHandler
    public void onFriendMessageEvent(FriendMessageEvent event) {
        final Friend friend = bot.getFriendOrFail(Code.ofLong(event.getFriend().getId()));
        final CompoundMessage compoundMessage = Mirais.toXiaoMing(event.getMessage(), Collections.singletonMap(Property.CONTACT, friend));
        
        final Event newEvent = new ReceiveFriendMessageEventImpl(
            friend,
            compoundMessage,
            TimeUnit.SECONDS.toMillis(event.getTime())
        );
        
        // cache message
        messageCache.put(new IntArray(event.getSource().getIds()), compoundMessage);
    
        bot.getEventManager().broadcastEvent(newEvent);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // recall event
    ///////////////////////////////////////////////////////////////////////////
    
    @EventHandler
    public void onGroupMessageRecallEvent(MessageRecallEvent.GroupRecall event) {
        // group
        final Group group = bot.getGroupOrFail(Code.ofLong(event.getGroup().getId()));
        
        // operator
        final UserOrBot operator;
        final net.mamoe.mirai.contact.Member miraiOperator = event.getOperator();
        if (Objects.isNull(miraiOperator)) {
            operator = bot;
        } else {
            operator = group.getMemberOrFail(Code.ofLong(miraiOperator.getId()));
        }
    
        final GroupMember sender = group.getMemberOrFail(Code.ofLong(event.getAuthorId()));
    
        final Event newEvent = new GroupMessageRecallEventImpl(
            group,
            messageCache.remove(new IntArray(event.getMessageIds())),
            sender,
            operator,
            TimeUnit.SECONDS.toMillis(event.getMessageTime())
        );
        bot.getEventManager().broadcastEvent(newEvent);
    }
    
    @EventHandler
    public void onFriendMessageRecallEvent(MessageRecallEvent.FriendRecall event) {
        // friend
        final Friend friend = bot.getFriendOrFail(Code.ofLong(event.getAuthorId()));
        
        final Event newEvent = new FriendMessageRecallEventImpl(
            friend,
            messageCache.remove(new IntArray(event.getMessageIds())),
            TimeUnit.SECONDS.toMillis(event.getMessageTime())
        );
        bot.getEventManager().broadcastEvent(newEvent);
    }
}