package cn.chuanwise.xiaoming.api.user;

import cn.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.api.recept.PrivateReceptionTask;
import net.mamoe.mirai.message.code.MiraiCode;

public interface PrivateXiaomingUser extends XiaomingUser<PrivateContact, PrivateMessage, PrivateReceptionTask> {
    void setReceptionTask(PrivateReceptionTask receptionTask);

    @Override
    default void nudge() {
        getContact().nudge();
    }

    @Override
    default PrivateMessage sendPrivateMessage(String message, Object... arguments) {
        return getContact().send(MiraiCode.deserializeMiraiCode(replaceArguments(message, arguments)));
    }
}