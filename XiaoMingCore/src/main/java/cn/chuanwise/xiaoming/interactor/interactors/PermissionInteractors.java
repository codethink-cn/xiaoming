package cn.chuanwise.xiaoming.interactor.interactors;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.Required;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.permission.PermissionService;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;

public class PermissionInteractors
        extends SimpleInteractors {
    PermissionService permissionService;

    @Override
    public void onRegister() {
        permissionService = xiaomingBot.getPermissionService();
    }

    @Filter(CommandWords.PERMISSION + CommandWords.SERVICE)
    @Required("core.permission.service.look")
    public void lookPermissionService(XiaomingUser user) {
        user.sendMessage("当前小明权限服务由" + Plugin.getChineseName(permissionService.getPlugin()) + "提供");
    }

    @Filter(CommandWords.RESET + CommandWords.PERMISSION + CommandWords.SERVICE)
    @Required("core.permission.service.look")
    public void resetPermissionService(XiaomingUser user) {
        if (permissionService.reset()) {
            user.sendMessage("成功重置权限请求器为小明内核默认的权限请求器");
        } else {
            user.sendMessage("当前权限服务本就是小明内核提供的");
        }
    }
}
