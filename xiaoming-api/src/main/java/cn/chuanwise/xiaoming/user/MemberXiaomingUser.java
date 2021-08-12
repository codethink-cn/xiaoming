package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.contact.message.MemberMessage;
import cn.chuanwise.xiaoming.recept.MemberReceptionTask;
import cn.chuanwise.xiaoming.group.GroupRecord;
import net.mamoe.mirai.message.code.MiraiCode;

public interface MemberXiaomingUser extends XiaomingUser<MemberContact, MemberMessage, MemberReceptionTask> {
    default GroupRecord getGroupRecord() {
        return getContact().getGroupRecord();
    }

    void setReceptionTask(MemberReceptionTask task);

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
    default MemberMessage sendPrivateMessage(String message, Object... arguments) {
        return getContact().send(MiraiCode.deserializeMiraiCode(replaceArguments(message, arguments)));
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
}