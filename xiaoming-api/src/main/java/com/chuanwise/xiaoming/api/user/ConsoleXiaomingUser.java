package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.object.HostObject;
import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.ArgumentUtil;

import java.util.Map;

public interface ConsoleXiaomingUser extends HostObject, XiaomingUser {
    @Override
    default boolean sendPrivateMessage(String message, Object... arguments) {
        getLog().info("私聊：" + ArgumentUtil.replaceArguments(message, arguments));
        return true;
    }

    @Override
    default boolean sendMessage(String message, Object... arguments) {
        getLog().info("消息：" + ArgumentUtil.replaceArguments(message, arguments));
        return true;
    }

    @Override
    default boolean sendGroupMessage(long group, String message, Object... arguments) {
        getLog().info("群聊(" + group + ")：" + ArgumentUtil.replaceArguments(message, arguments));
        return true;
    }

    @Override
    default boolean sendWarn(String message, Object... arguments) {
        getLog().warn(ArgumentUtil.replaceArguments(message, arguments));
        return true;
    }

    @Override
    default boolean sendError(String message, Object... arguments) {
        getLog().error(ArgumentUtil.replaceArguments(message, arguments));
        return true;
    }

    @Override
    default boolean sendPrivateError(String message, Object... arguments) {
        getLog().error("私聊错误：" + ArgumentUtil.replaceArguments(message, arguments));
        return true;
    }

    @Override
    default boolean sendPrivateWarn(String message, Object... arguments) {
        getLog().warn("私聊警告：" + ArgumentUtil.replaceArguments(message, arguments));
        return true;
    }

    @Override
    default boolean hasPermission(String nodes) {
        return true;
    }

    @Override
    default String getName() {
        return "后台";
    }
}
