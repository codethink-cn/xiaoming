package com.chuanwise.xiaoming.api.contact.contact;

import com.chuanwise.xiaoming.api.contact.ContactManager;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public interface GroupContact extends XiaomingContact {
    @Override
    Group getMiraiContact();

    default void atSend(long qq, String message) {
        send(new At(qq).plus(MiraiCode.deserializeMiraiCode(message)));
    }

    default void atSend(long qq, MessageChain messages) {
        send(new At(qq).plus(messages));
    }

    default void atSend(long qq, Message messages) {
        atSend(qq, messages.getMessageChain());
    }

    default void atSendLater(long timeout, long qq, String message) {
        sendLater(timeout, new At(qq).serializeToMiraiCode() + " " + message);
    }

    default void atSendLater(long timeout, long qq, MessageChain messages) {
        messages.add(0, new At(qq));
        sendLater(timeout, messages);
    }

    default void atSendLater(long timeout, long qq, Message messages) {
        atSendLater(timeout, qq, messages.getMessageChain());
    }

    default ResponseGroup getResponseGroup() {
        return getXiaomingBot().getResponseGroupManager().forCode(getCode());
    }

    default Set<String> getTags() {
        return getXiaomingBot().getResponseGroupManager().getTags(getCode());
    }

    @Override
    default String getCompleteName() {
        return getName() + "(" + getCodeString() + ")";
    }

    @Override
    default String getAvatarUrl() {
        return getMiraiContact().getAvatarUrl();
    }

    @Override
    default String getName() {
        return getMiraiContact().getName();
    }

    @Override
    default String getAlias() {
        final ResponseGroup responseGroup = getResponseGroup();
        return Objects.nonNull(responseGroup) ? responseGroup.getAlias() : getName();
    }

    /**
     * 获得群成员信息
     * @param qq 群成员 QQ
     * @return 群成员信息。如果没有找到，返回 null
     */
    default TempContact getMember(long qq) {
        return getXiaomingBot().getContactManager().getTempContact(getCode(), qq);
    }

    default boolean quit() {
        return getMiraiContact().quit();
    }

    @Override
    List<GroupMessage> getRecentMessages();

    default void addRecentMessage(GroupMessage message) {
        final ContactManager contactManager = getXiaomingBot().getContactManager();
        for (String tag : getTags()) {
            final List<GroupMessage> list = contactManager.getOrPutGroupRecentMessages(tag);
            list.add(message);
            synchronized (list) {
                list.notifyAll();
            }
        }
    }
}
