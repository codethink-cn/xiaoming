package cn.chuanwise.xiaoming.permission;

import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface PermissionService
        extends ModuleObject, PermissionRequester {
    boolean register(PermissionRequester requester, Plugin plugin);

    Plugin getPlugin();

    PermissionRequester getPermissionRequester();

    PermissionRequester getCorePermissionRequester();

    boolean reset();

    default boolean isSet() {
        return Objects.nonNull(getPlugin());
    }

    default boolean hasPermission(long authorierCode, String permission) {
        return hasPermission(authorierCode, Permission.compile(permission));
    }

    default boolean hasPermission(long authorierCode, Permission permission) {
        return hasPermission(getXiaomingBot().getAccountManager().createAccount(authorierCode), permission);
    }

    boolean hasPermission(long authorierCode, long groupCode, Permission permission);

    default boolean hasPermission(long authorierCode, long groupCode, String permission) {
        return hasPermission(authorierCode, groupCode, Permission.compile(permission));
    }
}