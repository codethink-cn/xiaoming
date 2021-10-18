package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import net.mamoe.mirai.message.code.MiraiCode;

import java.util.Optional;

public interface PrivateXiaomingUser extends XiaomingUser<PrivateContact> {
    @Override
    default void nudge() {
        getContact().nudge();
    }

    @Override
    default Optional<Message> sendPrivateMessage(String message, Object... arguments) {
        return getContact().sendMessage(MiraiCode.deserializeMiraiCode(format(message, arguments)));
    }
}