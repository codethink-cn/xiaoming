package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.permission.PermissionUserNode;
import cn.chuanwise.xiaoming.user.XiaomingUser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DebugInteractors extends SimpleInteractors {
    @Filter("debug1 {arg1}")
    @Permission("debug.{args.arg1}")
    public void onDebug1(XiaomingUser user, @FilterParameter("arg1") String args) {
        user.sendMessage(args);
    }

    @Filter("唱歌")
    public void onSingSong(XiaomingUser user) {
        if (user.hasPermission("xxx")) {
            // 有权限
        } else {
            // 没权限
        }

        if (user.requirePermission("xxx")) {
            // 有权限
        } else {
            // 用户收到一句 小明不能帮你做这件事xxx
            // 没权限
        }

        final List<Long> ownerCodes = xiaomingBot.getPermissionManager()
                .getUsers()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().getPermissions().contains("*"))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

    }
}
