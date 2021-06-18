package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.contact.contact.MemberContact;
import com.chuanwise.xiaoming.api.contact.message.ConsoleMessage;
import com.chuanwise.xiaoming.api.contact.message.MemberMessage;
import com.chuanwise.xiaoming.api.recept.MemberReceptionTask;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import net.mamoe.mirai.message.code.MiraiCode;

public interface MemberXiaomingUser extends XiaomingUser<MemberContact, MemberMessage, MemberReceptionTask> {
    default ResponseGroup getResponseGroup() {
        return getContact().getResponseGroup();
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
}