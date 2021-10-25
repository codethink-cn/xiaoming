package cn.chuanwise.xiaoming.interactor.interactors;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Required;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.permission.CorePermissionRequester;
import cn.chuanwise.xiaoming.permission.Permission;
import cn.chuanwise.xiaoming.permission.PermissionService;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.plugin.PluginHandler;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PermissionInteractors
        extends SimpleInteractors {
    PermissionService permissionService;

    @Override
    public void onRegister() {
        permissionService = xiaomingBot.getPermissionService();
    }

    @Filter(CommandWords.PERMISSION + CommandWords.SERVICE)
    @Required("permission.service.look")
    public void lookPermissionService(XiaomingUser user) {
        user.sendMessage("当前小明权限服务由" + Plugin.getChineseName(permissionService.getPlugin()) + "提供");
    }

    @Filter(CommandWords.RESET + CommandWords.PERMISSION + CommandWords.SERVICE)
    @Required("permission.service.look")
    public void resetPermissionService(XiaomingUser user) {
        if (permissionService.reset()) {
            user.sendMessage("成功重置权限请求器为小明内核默认的权限请求器");
        } else {
            user.sendMessage("当前权限服务本就是小明内核提供的");
        }
    }

    @Filter(CommandWords.USER + CommandWords.PERMISSION)
    @Required("permission.user.list")
    public void listUserPermissions(XiaomingUser user) {
        if (permissionService.isSet()) {
            user.sendError("当前权限服务并非小明内核提供，此项无效");
            return;
        }

        final List<Permission> userPermissions = CollectionUtil.copyOf(permissionService.getCorePermissionRequester().getUserPermissions());
        final Set<Permission> pluginPermissions = xiaomingBot.getPluginManager()
                .getPlugins()
                .values()
                .stream()
                .map(Plugin::getHandler)
                .map(PluginHandler::getUserPermissions)
                .flatMap(Stream::of)
                .collect(Collectors.toUnmodifiableSet());
        userPermissions.addAll(pluginPermissions);

        if (userPermissions.isEmpty()) {
            user.sendError("没有任何开放的用户权限");
        } else {
            Collections.sort(userPermissions);
            user.sendMessage("下列权限开放给普通用户：\n" +
                    CollectionUtil.toIndexString(userPermissions));
        }
    }

    @Filter(CommandWords.ADD + CommandWords.USER + CommandWords.PERMISSION + " {r:权限节点}")
    @Filter(CommandWords.NEW + CommandWords.USER + CommandWords.PERMISSION + " {r:权限节点}")
    @Required("permission.user.add")
    public void addUserPermissions(XiaomingUser user, @FilterParameter("权限节点") Permission permission) {
        if (permissionService.isSet()) {
            user.sendError("当前权限服务并非小明内核提供，此项无效");
            return;
        }

        final CorePermissionRequester corePermissionRequester = permissionService.getCorePermissionRequester();
        final List<Permission> userPermissions = corePermissionRequester.getUserPermissions();
        if (userPermissions.contains(permission)) {
            user.sendError("权限节点「" + permission + "」已经开放给用户了");
        } else {
            userPermissions.add(permission);
            xiaomingBot.getFileSaver().readyToSave(corePermissionRequester);
            user.sendMessage("成功将权限节点「" + permission + "」开放给用户");
        }
    }

    @Filter(CommandWords.REMOVE + CommandWords.USER + CommandWords.PERMISSION + " {r:权限节点}")
    @Required("permission.user.remove")
    public void removeUserPermissions(XiaomingUser user, @FilterParameter("权限节点") Permission permission) {
        if (permissionService.isSet()) {
            user.sendError("当前权限服务并非小明内核提供，此项无效");
            return;
        }

        final CorePermissionRequester corePermissionRequester = permissionService.getCorePermissionRequester();
        final List<Permission> userPermissions = corePermissionRequester.getUserPermissions();
        if (userPermissions.contains(permission)) {
            userPermissions.remove(permission);
            xiaomingBot.getFileSaver().readyToSave(corePermissionRequester);
            user.sendMessage("成功将权限节点「" + permission + "」回收");
        } else {
            user.sendError("权限节点「" + permission + "」并没有被开放给用户。这可能是插件开放给用户的。\n" +
                    "需更完善的权限管理功能，请安装 Permission 插件");
        }
    }
}
