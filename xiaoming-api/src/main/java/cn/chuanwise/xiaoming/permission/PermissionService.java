package cn.chuanwise.xiaoming.permission;

import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface PermissionService
        extends ModuleObject, PermissionRequester {
    void register(PermissionRequester requester, Plugin plugin);

    Plugin getPlugin();

    PermissionRequester getPermissionRequester();

    CorePermissionRequester getCorePermissionRequester();

    void reset();

    default boolean isSet() {
        return Objects.nonNull(getPlugin());
    }

    default boolean hasPermission(long userCode, String permissionNode) {
        return hasPermission(getXiaomingBot().getAccountManager().createAccount(userCode), permissionNode);
    }

    boolean hasPermission(long userCode, long groupCode, String permissionNode);
}
