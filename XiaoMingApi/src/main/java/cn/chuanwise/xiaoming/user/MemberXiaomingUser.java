package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.group.GroupInformation;
import net.mamoe.mirai.message.code.MiraiCode;

import java.util.Optional;

public interface MemberXiaomingUser extends XiaomingUser<MemberContact> {
    default GroupInformation getGroupRecord() {
        return getContact().getGroupRecord();
    }

    @Override
    default void nudge() {
        getContact().nudge();
    }

    default long getGroupCode() {
        return getContact().getGroupCode();
    }

    default String getGroupCodeString() {
        return getContact().getGroupCodeString();
    }

    @Override
    default Optional<Message> sendPrivateMessage(String message, Object... arguments) {
        return getContact().sendMessage(MiraiCode.deserializeMiraiCode(format(message, arguments)));
    }

    default GroupContact getGroupContact() {
        return getContact().getGroupContact();
    }

    default String getNick() {
        return getContact().getNick();
    }

    default String getNameCard() {
        return getContact().getNameCard();
    }

    @Override
    default String getName() {
        return getContact().getName();
    }
}