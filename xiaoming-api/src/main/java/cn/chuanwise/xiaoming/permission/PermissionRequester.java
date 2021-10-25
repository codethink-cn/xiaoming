package cn.chuanwise.xiaoming.permission;

import cn.chuanwise.api.Flushable;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.group.GroupInformation;
import cn.chuanwise.xiaoming.user.XiaomingUser;

public interface PermissionRequester extends Flushable {
    boolean hasPermission(XiaomingUser user, Permission permission);

    default boolean hasPermission(XiaomingUser user, String permission) {
        return hasPermission(user, Permission.compile(permission));
    }

    boolean hasPermission(Account account, Permission permission);

    boolean hasPermission(Account account, GroupInformation groupInformation, Permission permission);

    default boolean hasPermission(Account account, GroupInformation groupInformation, String permission) {
        return hasPermission(account, groupInformation, Permission.compile(permission));
    }

    @Override
    default void flush() {}

    default void onDisable() {}

    default void onEnable() {}
}