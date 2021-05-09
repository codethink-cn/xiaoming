package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.object.HostXiaomingObject;

public interface ConsoleXiaomingUser extends HostXiaomingObject, XiaomingUser, GroupXiaomingUser, PrivateXiaomingUser {
    @Override
    default boolean sendMessage(String message, Object... arguments) {
        getLog().info(message, arguments);
        return true;
    }

    @Override
    default boolean sendError(String message, Object... arguments) {
        getLog().error(message, arguments);
        return true;
    }

    @Override
    default boolean sendWarning(String message, Object... arguments) {
        getLog().warn(message, arguments);
        return true;
    }

    @Override
    default boolean hasPermission(String node) {
        return true;
    }

    @Override
    default String getName() {
        return "后台";
    }
}
