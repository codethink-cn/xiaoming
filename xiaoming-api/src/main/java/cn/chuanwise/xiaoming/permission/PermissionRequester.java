package cn.chuanwise.xiaoming.permission;

import cn.chuanwise.api.Flushable;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.group.GroupInformation;
import cn.chuanwise.xiaoming.user.XiaomingUser;

public interface PermissionRequester extends Flushable {
    boolean hasPermission(XiaomingUser user, String permission);

    boolean hasPermission(Account account, String permission);

    boolean hasPermission(Account account, GroupInformation groupInformation, String permission);

    @Override
    default void flush() {}
}