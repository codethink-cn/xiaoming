package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.account.record.MemberCommandRecord;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.group.GroupRecord;
import net.mamoe.mirai.message.code.MiraiCode;

public interface MemberXiaomingUser extends XiaomingUser<MemberContact> {
    default GroupRecord getGroupRecord() {
        return getContact().getGroupRecord();
    }

    @Override
    default MemberCommandRecord buildCommandRecord(String command) {
        return new MemberCommandRecord(getGroupCode(), command);
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
    default Message sendPrivateMessage(String message, Object... arguments) {
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