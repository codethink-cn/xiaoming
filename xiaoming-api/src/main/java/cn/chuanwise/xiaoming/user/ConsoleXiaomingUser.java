package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.contact.contact.ConsoleContact;
import cn.chuanwise.xiaoming.contact.message.ConsoleMessage;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.recept.ConsoleReceptionTask;

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
        getLogger().info(replacedMessage);
    }

    @Override
    default void sendError(String message, Object... arguments) {
        final String replacedMessage = replaceArguments(message, arguments);
        getLogger().error(replacedMessage);
    }

    @Override
    default void sendWarning(String message, Object... arguments) {
        final String replacedMessage = replaceArguments(message, arguments);
        getLogger().warn(replacedMessage);
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
