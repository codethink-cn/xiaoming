package com.chuanwise.xiaoming.api.permission;

import com.chuanwise.xiaoming.api.object.XiaomingObject;

import java.util.List;

/**
 * 用户权限信息节点
 */
public interface PermissionUserNode {
    /**
     * 为用户授权
     * @param node 权限节点
     */
    void addPermission(String node);

    /**
     * 判断用户是否有特有权限
     * @param node
     * @return
     */
    boolean hasPrivatePermission(String node);

    String getGroup();

    List<String> getPermissions();

    void setPermissions(List<String> permissions);

    void setGroup(String group);
}
