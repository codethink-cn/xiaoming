package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.account.record.PrivateCommandRecord;
import cn.chuanwise.xiaoming.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import net.mamoe.mirai.message.code.MiraiCode;

public interface PrivateXiaomingUser extends XiaomingUser<PrivateContact> {
    @Override
    default PrivateCommandRecord buildCommandRecord(String command) {
        return new PrivateCommandRecord(command);
    }

    @Override
    default void nudge() {
        getContact().nudge();
    }

    @Override
    default Message sendPrivateMessage(String message, Object... arguments) {
        return getContact().sendMessage(MiraiCode.deserializeMiraiCode(format(message, arguments)));
    }
}