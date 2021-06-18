package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.contact.contact.ConsoleContact;
import com.chuanwise.xiaoming.api.contact.message.ConsoleMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import com.chuanwise.xiaoming.api.object.ModuleObject;
import com.chuanwise.xiaoming.api.recept.ConsoleReceptionTask;
import com.chuanwise.xiaoming.api.util.ArgumentUtils;
import net.mamoe.mirai.message.code.MiraiCode;

import java.util.List;

public interface ConsoleXiaomingUser extends ModuleObject, XiaomingUser<ConsoleContact, ConsoleMessage, ConsoleReceptionTask> {
    void setReceptionTask(ConsoleReceptionTask receptionTask);

    @Override
    default boolean hasPermission(String require) {
        return true;
    }

    @Override
    default String getName() {
        return "后台";
    }

    @Override
    default void sendMessage(String message, Object... arguments) {
        final String replacedMessage = replaceArguments(message, arguments);
        getLog().info(replacedMessage);
    }

    @Override
    default void sendError(String message, Object... arguments) {
        final String replacedMessage = replaceArguments(message, arguments);
        getLog().error(replacedMessage);
    }

    @Override
    default void sendWarning(String message, Object... arguments) {
        final String replacedMessage = replaceArguments(message, arguments);
        getLog().warn(replacedMessage);
    }

    @Override
    default void sendPrivateWarning(String message, Object... arguments) {
        sendWarning(message, arguments);
    }

    @Override
    default void sendPrivateError(String message, Object... arguments) {
        sendError(message, arguments);
    }

    @Override
    default void nudge() {
        sendWarning("戳了戳你");
    }
}
