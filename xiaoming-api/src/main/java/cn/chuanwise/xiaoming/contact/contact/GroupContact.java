package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.xiaoming.attribute.AttributeType;
import cn.chuanwise.xiaoming.contact.ContactManager;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.group.GroupRecord;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.group.GroupRecordManager;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

import cn.chuanwise.xiaoming.user.XiaomingUser;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

public interface GroupContact extends XiaomingContact<Group> {
    default Message atSend(long code, String message) {
        return atSend(code, MiraiCode.deserializeMiraiCode(getXiaomingBot().getLanguageManager().format(message)));
    }

    default Message atSend(long code, MessageChain messages) {
        return sendMessage(new At(code).plus(" ").plus(messages));
    }

    default Message atSend(long code, Message messages) {
        return atSend(code, messages.getMessageChain());
    }

    default GroupRecord getGroupRecord() {
        final GroupRecordManager groupRecordManager = getXiaomingBot().getGroupRecordManager();
        GroupRecord groupRecord = groupRecordManager.forCode(getCode());
        if (Objects.isNull(groupRecord)) {
            groupRecord = groupRecordManager.addGroup(getCode(), getName());
        }
        return groupRecord;
    }

    @Override
    default Optional<Message> nextMessage(long timeout) throws InterruptedException {
        return getXiaomingBot()
                .getContactManager()
                .nextGroupMessage(getCode(), timeout)
                .map(MessageEvent::getMessage);
    }

    @Override
    default String getAliasAndCode() {
        return getAlias() + "（" + getCodeString() + "）";
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
        final GroupRecord groupRecord = getGroupRecord();
        return Objects.nonNull(groupRecord) ? groupRecord.getAlias() : getName();
    }

    default Message atReply(Message quote, long target, MessageChain messages) {
        return reply(quote, new At(target).plus(" ").plus(messages));
    }

    default Message atReply(Message quote, long target, String message) {
        return atReply(quote, target, MiraiCode.deserializeMiraiCode(message));
    }

    default Message atReply(Message quote, long target, Message message) {
        return atReply(quote, target, message.getMessageChain());
    }

    /**
     * 获得群成员信息
     * @param qq 群成员 QQ
     * @return 群成员信息。如果没有找到，返回 null
     */
    default MemberContact getMember(long qq) {
        return getXiaomingBot().getContactManager().getMemberContact(getCode(), qq);
    }

    default MemberContact getBotMember() {
        return getXiaomingBot().getContactManager().getMemberContact(this, getMiraiContact().getBotAsMember());
    }

    default MemberContact getOwner() {
        return getXiaomingBot().getContactManager().getMemberContact(this, getMiraiContact().getOwner());
    }

    default List<MemberContact> getMembers() {
        final ContactList<NormalMember> members = getMiraiContact().getMembers();
        final List<MemberContact> memberContacts = new ArrayList<>(members.size());
        final ContactManager contactManager = getXiaomingBot().getContactManager();

        for (NormalMember member : members) {
            memberContacts.add(contactManager.getMemberContact(this, member));
        }
        return memberContacts;
    }

    default boolean quit() {
        return getMiraiContact().quit();
    }

    default void setName(String name) {
        getMiraiContact().setName(name);
    }

    default GroupSettings getSettings() {
        return getMiraiContact().getSettings();
    }

    @Override
    default Set<String> getTags() {
        return getXiaomingBot().getGroupRecordManager().getTags(getCode());
    }
}
