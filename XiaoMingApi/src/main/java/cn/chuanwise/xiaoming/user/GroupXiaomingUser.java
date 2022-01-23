package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.group.GroupInformation;

import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.Optional;

public interface GroupXiaomingUser extends XiaomingUser<GroupContact> {
    MemberContact getMemberContact();

    default boolean isMuted() {
        return getMemberContact().isMuted();
    }

    default String getNick() {
        return getMemberContact().getNick();
    }

    default String getNameCard() {
        return getMemberContact().getNameCard();
    }

    default void setNameCard(String nameCard) {
        getMemberContact().setNameCard(nameCard);
    }

    default long getGroupCode() {
        return getContact().getCode();
    }

    default String getGroupCodeString() {
        return getContact().getCodeString();
    }

    default GroupInformation getGroupInformation() {
        return getContact().getGroupInformation();
    }


    default Optional<Message> atReply(Message quote, String message) {
        return atReply(quote, MiraiCode.deserializeMiraiCode(message));
    }

    default Optional<Message> atReply(Message quote, Message message) {
        return atReply(quote, message.getMessageChain());
    }

    default Optional<Message> atReply(Message quote, MessageChain message) {
        return getContact().atReply(quote, getCode(), message);
    }


    default Optional<Message> sendGroupMessage(Message message) {
        return sendGroupMessage(message.getMessageChain());
    }

    default Optional<Message> sendGroupMessage(MessageChain message) {
        return getContact().sendMessage(message);
    }

    default Optional<Message> sendGroupMessage(String message) {
        return sendGroupMessage(MiraiCode.deserializeMiraiCode(getXiaomingBot().getLanguageManager().format(message)));
    }

    @Override
    default void nudge() {
        getMemberContact().nudge();
    }

    default void mute(long timeMillis) {
        getMemberContact().mute(timeMillis);
    }

    default void unmute() {
        getMemberContact().unmute();
    }

    @Override
    default Optional<Message> sendPrivateMessage(String message, Object... arguments) {
        return getMemberContact().sendMessage(MiraiCode.deserializeMiraiCode(format(message, arguments)));
    }
}
