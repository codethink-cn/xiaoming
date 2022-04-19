package cn.codethink.xiaoming.util;

import cn.chuanwise.common.util.Preconditions;
import cn.chuanwise.common.util.StaticUtilities;
import cn.codethink.xiaoming.QqBot;
import cn.codethink.xiaoming.annotation.InternalAPI;
import cn.codethink.xiaoming.contact.QqFriend;
import cn.codethink.xiaoming.contact.QqGroup;
import cn.codethink.xiaoming.contact.QqMember;
import cn.codethink.xiaoming.contact.QqStranger;
import cn.codethink.xiaoming.event.*;
import cn.codethink.xiaoming.exception.CancelledException;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.receipt.MessageReceipt;
import cn.codethink.xiaoming.message.metadata.*;
import cn.codethink.xiaoming.property.Property;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.contact.Stranger;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Collections;
import java.util.Map;

/**
 * 和 qq 会话相关的工具
 *
 * @author Chuanwise
 */
@InternalAPI
public class QqContacts
    extends StaticUtilities {
    
    /**
     * 发送好友消息
     *
     * @param message 消息
     * @param friend  好友
     * @return 消息回执
     * @throws CancelledException 发送事件被取消
     * @throws NullPointerException                           message 或 friend 为 null
     * @see cn.codethink.xiaoming.contact.Friend#sendMessage(Message)
     * @see cn.codethink.xiaoming.contact.Friend#sendMessage(String)
     */
    public static MessageReceipt sendFriendMessage(Message message, QqFriend friend) {
        Preconditions.objectNonNull(message, "message");
        Preconditions.objectNonNull(friend, "friend");
        
        final QqBot bot = (QqBot) friend.getBot();
    
        // pre send
        final Friend qqFriend = friend.getQqFriend();
        final PreSendMessageEvent event = new PreSendFriendMessageEventImpl(friend, message);
        bot.getEventManager().broadcastEventSync(event);
        if (event.isCancelled()) {
            throw new CancelledException(bot);
        }
    
        // send
        final Map<Property<?>, Object> properties = Collections.singletonMap(Property.CONTACT, friend);
        final net.mamoe.mirai.message.MessageReceipt<Friend> messageReceipt =
            qqFriend.sendMessage(Qqs.toQq(message.asCompoundMessage(), properties));
        final ToFriendMessageSource messageSource = new QqToFriendMessageSource((OnlineMessageSource.Outgoing.ToFriend) messageReceipt.getSource(), friend, properties);
        final CompoundMessage compoundMessage = message.plus(messageSource);
    
        // post send
        final PostSendMessageEvent postSendMessageEvent = new PostSendFriendMessageEventImpl(friend, compoundMessage, messageSource);
        bot.getEventManager().broadcastEvent(postSendMessageEvent);
        return postSendMessageEvent;
    }
    
    /**
     * 发送群聊消息
     *
     * @param message 消息
     * @param group   群聊
     * @return 消息回执
     * @throws CancelledException 发送事件被取消
     * @throws NullPointerException                           message 或 group 为 null
     * @see cn.codethink.xiaoming.contact.Group#sendMessage(Message)
     * @see cn.codethink.xiaoming.contact.Group#sendMessage(String)
     */
    public static MessageReceipt sendGroupMessage(Message message, QqGroup group) {
        Preconditions.objectNonNull(message, "message");
        Preconditions.objectNonNull(group, "group");
    
        final QqBot bot = (QqBot) group.getBot();
    
        // pre send
        final Group qqGroup = group.getQqGroup();
        final PreSendMessageEvent event = new PreSendGroupMessageEventImpl(group, message);
        bot.getEventManager().broadcastEventSync(event);
        if (event.isCancelled()) {
            throw new CancelledException(bot);
        }
        message = event.getMessage();
    
        // send message
        final Map<Property<?>, Object> properties = Collections.singletonMap(Property.CONTACT, group);
        final net.mamoe.mirai.message.MessageReceipt<Group> messageReceipt =
            qqGroup.sendMessage(Qqs.toQq(message.asCompoundMessage(), properties));
        final ToGroupMessageSource messageSource = new QqToGroupMessageSource((OnlineMessageSource.Outgoing.ToGroup) messageReceipt.getSource(), group, properties);
        final CompoundMessage compoundMessage = message.plus(messageSource);
    
        // post send
        final PostSendMessageEvent postSendMessageEvent = new PostSendGroupMessageEventImpl(group, compoundMessage, messageSource);
        bot.getEventManager().broadcastEvent(postSendMessageEvent);
        return postSendMessageEvent;
    }
    
    /**
     * 发送群临时会话消息
     *
     * @param message 消息
     * @param member  群员
     * @return 消息回执
     * @throws CancelledException 发送事件被取消
     * @throws NullPointerException                           message 或 member 为 null
     * @see cn.codethink.xiaoming.contact.Member#sendMessage(Message)
     * @see cn.codethink.xiaoming.contact.Member#sendMessage(String)
     */
    public static MessageReceipt sendGroupMemberMessage(Message message, QqMember member) {
        Preconditions.objectNonNull(message, "message");
        Preconditions.objectNonNull(member, "group");
    
        final QqBot bot = (QqBot) member.getBot();
    
        // pre send
        final NormalMember qqMember = member.getQqMember();
        final PreSendMessageEvent event = new PreSendGroupMemberMessageEventImpl(member, message);
        bot.getEventManager().broadcastEventSync(event);
        if (event.isCancelled()) {
            throw new CancelledException(bot);
        }
        message = event.getMessage();
    
        // send message
        final Map<Property<?>, Object> properties = Collections.singletonMap(Property.CONTACT, member);
        final net.mamoe.mirai.message.MessageReceipt<NormalMember> messageReceipt =
            qqMember.sendMessage(Qqs.toQq(message.asCompoundMessage(), properties));
        final ToGroupMemberMessageSource messageSource = new QqToGroupMemberMessageSource((OnlineMessageSource.Outgoing.ToTemp) messageReceipt.getSource(), member, properties);
        final CompoundMessage compoundMessage = message.plus(messageSource);
    
        // post send
        final PostSendMessageEvent postSendMessageEvent = new PostSendGroupMemberMessageEventImpl(member, compoundMessage, messageSource);
        bot.getEventManager().broadcastEvent(postSendMessageEvent);
        return postSendMessageEvent;
    }
    
    /**
     * 发送陌生人消息
     *
     * @param message  消息
     * @param stranger 陌生人
     * @return 消息回执
     * @throws CancelledException 发送事件被取消
     * @throws NullPointerException                           message 或 stranger 为 null
     * @see cn.codethink.xiaoming.contact.Stranger#sendMessage(Message)
     * @see cn.codethink.xiaoming.contact.Stranger#sendMessage(String)
     */
    public static MessageReceipt sendStrangerMessage(Message message, QqStranger stranger) {
        Preconditions.objectNonNull(message, "message");
        Preconditions.objectNonNull(stranger, "group");
    
        final QqBot bot = (QqBot) stranger.getBot();
    
        // pre send
        final Stranger qqStranger = stranger.getQqStranger();
        final PreSendMessageEvent event = new PreSendStrangerMessageEventImpl(stranger, message);
        bot.getEventManager().broadcastEventSync(event);
        if (event.isCancelled()) {
            throw new CancelledException(bot);
        }
        message = event.getMessage();
        
        // send message
        final Map<Property<?>, Object> properties = Collections.singletonMap(Property.CONTACT, stranger);
        final net.mamoe.mirai.message.MessageReceipt<Stranger> messageReceipt =
            qqStranger.sendMessage(Qqs.toQq(message.asCompoundMessage(), properties));
        final ToStrangerMessageSource messageSource = new QqToStrangerMessageSource(messageReceipt.getSource(), stranger, properties);
        final CompoundMessage compoundMessage = message.plus(messageSource);
    
        // post
        final PostSendMessageEvent postSendMessageEvent = new PostSendStrangerMessageEventImpl(stranger, compoundMessage, messageSource);
        bot.getEventManager().broadcastEvent(postSendMessageEvent);
        return postSendMessageEvent;
    }
}