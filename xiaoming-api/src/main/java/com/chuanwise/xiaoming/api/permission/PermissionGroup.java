package com.chuanwise.xiaoming.api.permission;

import com.chuanwise.xiaoming.api.object.XiaomingObject;

import java.util.List;

/**
 * 小明核心权限组对象
 * @author Chuanwise
 */
public interface PermissionGroup {
    /**
     * 为权限组增加权限
     * @param node
     */
    void addPermission(String node);

    /**
     * 删除权限组的权限
     * @param node
     */
    void removePermission(String node);

    List<String> getSuperGroups();

    String getAlias();

    List<String> getPermissions();

    void setAlias(String alias);

    default void addSuperGroup(String groupName) {
        getSuperGroups().add(groupName);
    }

    void setPermissions(List<String> permissions);
}
