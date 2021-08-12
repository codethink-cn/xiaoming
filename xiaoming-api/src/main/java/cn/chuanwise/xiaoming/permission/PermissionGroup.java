package cn.chuanwise.xiaoming.permission;

import cn.chuanwise.utility.StringUtility;

import java.util.List;
import java.util.Map;

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

    Map<String, List<String>> getGroupPermissions();

    List<String> getGroupPermission(String tag);

    List<String> getOrPutGroupPermission(String tag);

    List<String> getSuperGroups();

    String getAlias();

    default String getAliasAndName() {
        if (StringUtility.nonEmpty(getAlias())) {
            return getAlias() + "（" + getName() + "）";
        } else {
            return getName();
        }
    }

    List<String> getPermissions();

    void setAlias(String alias);

    default void addSuperGroup(String groupName) {
        getSuperGroups().add(groupName);
    }

    void setPermissions(List<String> permissions);

    void setName(String name);

    String getName();
}
