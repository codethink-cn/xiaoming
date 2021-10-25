package cn.chuanwise.xiaoming.permission;

import cn.chuanwise.toolkit.preservable.Preservable;

import java.util.List;

public interface CorePermissionRequester
        extends PermissionRequester, Preservable {
    List<Permission> getUserPermissions();
}
