package cn.chuanwise.xiaoming.api.permission;

import java.util.List;
import java.util.Map;

/**
 * 用户权限信息节点
 */
public interface PermissionUserNode {
    /**
     * 为用户授权
     * @param node 权限节点
     */
    default void addPermission(String node) {
        getPermissions().add(node);
    }

    default List<String> getGroupPermission(String tag) {
        return getGroupPermissions().get(tag);
    }

    Map<String, List<String>> getGroupPermissions();

    List<String> getOrPutGroupPermission(String tag);

    String getGroup();

    List<String> getPermissions();

    void setPermissions(List<String> permissions);

    void setGroup(String group);
}
