package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.contact.contact.ConsoleContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.object.ModuleObject;

import java.util.Optional;

public interface ConsoleXiaomingUser extends ModuleObject, XiaomingUser<ConsoleContact> {
    @Override
    default boolean hasPermission(String permission) {
        return true;
    }

    @Override
    default String getName() {
        return "后台";
    }

    @Override
    default Optional<Message> sendMessage(String message, Object... arguments) {
        final String replacedMessage = format(message, arguments);
        getLogger().info(replacedMessage);
        return Optional.empty();
    }

    @Override
    default Optional<Message> sendError(String message, Object... arguments) {
        final String replacedMessage = format(message, arguments);
        getLogger().error(replacedMessage);
        return Optional.empty();
    }

    @Override
    default Optional<Message> sendWarning(String message, Object... arguments) {
        final String replacedMessage = format(message, arguments);
        getLogger().warn(replacedMessage);
        return Optional.empty();
    }

    @Override
    default void nudge() {
        sendWarning("戳了戳你");
    }
}
