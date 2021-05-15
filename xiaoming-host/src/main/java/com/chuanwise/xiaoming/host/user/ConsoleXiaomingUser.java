package com.chuanwise.xiaoming.host.user;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.ArgumentUtil;
import com.chuanwise.xiaoming.core.user.XiaomingUserImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleXiaomingUser extends XiaomingUserImpl {
    public ConsoleXiaomingUser(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    @Override
    public boolean sendPrivateMessage(String message, Object... arguments) {
        getLog().info(ArgumentUtil.replaceArguments("私聊：" + message, arguments));
        return true;
    }

    @Override
    public boolean sendGroupMessage(long group, String message, Object... arguments) {
        getLog().info(ArgumentUtil.replaceArguments("群聊(" + group + ")：" + message, arguments));
        return true;
    }

    @Override
    public boolean sendWarn(String message, Object... arguments) {
        getLog().warn(ArgumentUtil.replaceArguments(message, arguments));
        return true;
    }

    @Override
    public boolean sendError(String message, Object... arguments) {
        getLog().error(ArgumentUtil.replaceArguments(message, arguments));
        return true;
    }

    @Override
    public boolean sendPrivateError(String message, Object... arguments) {
        getLog().error("私聊错误：" + ArgumentUtil.replaceArguments(message, arguments));
        return true;
    }

    @Override
    public boolean sendPrivateWarn(String message, Object... arguments) {
        getLog().warn("私聊警告：" + ArgumentUtil.replaceArguments(message, arguments));
        return true;
    }

    @Override
    public boolean hasPermission(String nodes) {
        return true;
    }

    @Override
    public String getName() {
        return "后台";
    }
}
