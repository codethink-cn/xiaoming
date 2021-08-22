package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.account.record.CommandRecord;
import cn.chuanwise.xiaoming.account.record.PrivateCommandRecord;
import cn.chuanwise.xiaoming.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.recept.PrivateReceptionTask;
import net.mamoe.mirai.message.code.MiraiCode;

public interface PrivateXiaomingUser extends XiaomingUser<PrivateContact, PrivateMessage, PrivateReceptionTask> {
    void setReceptionTask(PrivateReceptionTask receptionTask);

    @Override
    default PrivateCommandRecord buildCommandRecord(String command) {
        return new PrivateCommandRecord(command);
    }

    @Override
    default void nudge() {
        getContact().nudge();
    }

    @Override
    default PrivateMessage sendPrivateMessage(String message, Object... arguments) {
        return getContact().send(MiraiCode.deserializeMiraiCode(format(message, arguments)));
    }
}