package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.contact.contact.ConsoleContact;
import com.chuanwise.xiaoming.api.contact.message.ConsoleMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.object.ModuleObject;
import com.chuanwise.xiaoming.api.recept.ConsoleReceptionTask;
import com.chuanwise.xiaoming.api.util.ArgumentUtils;

import java.util.List;

public interface ConsoleXiaomingUser extends ModuleObject, XiaomingUser<ConsoleContact, ConsoleMessage, ConsoleReceptionTask> {
    void setReceptionTask(ConsoleReceptionTask receptionTask);

    @Override
    default void sendMessage(String message, Object... arguments) {
        getLog().info("消息：" + ArgumentUtils.replaceArguments(ArgumentUtils.replaceArguments(message, getProperties(), getXiaomingBot().getConfiguration().getMaxIterateTime()), arguments));
    }

    @Override
    default void sendWarning(String message, Object... arguments) {
        getLog().warn(ArgumentUtils.replaceArguments(ArgumentUtils.replaceArguments(message, getProperties(), getXiaomingBot().getConfiguration().getMaxIterateTime()), arguments));
    }

    @Override
    default void sendError(String message, Object... arguments) {
        getLog().error(ArgumentUtils.replaceArguments(ArgumentUtils.replaceArguments(message, getProperties(), getXiaomingBot().getConfiguration().getMaxIterateTime()), arguments));
    }

    @Override
    default boolean hasPermission(String require) {
        return true;
    }

    @Override
    default void sendPrivateMessage(String message, Object... arguments) {
        sendMessage(message, arguments);
    }

    @Override
    default void sendPrivateError(String message, Object... arguments) {
        sendError(message, arguments);
    }

    @Override
    default void sendPrivateWarning(String message, Object... arguments) {
        sendWarning(message, arguments);
    }

    @Override
    default String getName() {
        return "后台";
    }
}
