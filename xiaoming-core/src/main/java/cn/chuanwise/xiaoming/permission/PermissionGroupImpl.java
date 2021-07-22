package cn.chuanwise.xiaoming.permission;

import cn.chuanwise.xiaoming.permission.PermissionGroup;
import lombok.Data;

import java.util.*;

@Data
public class PermissionGroupImpl implements PermissionGroup {
    List<String> superGroups = new ArrayList<>();
    String alias;
    List<String> permissions = new ArrayList<>();
    Map<String, List<String>> groupPermissions = new HashMap<>();
    String name;

    @Override
    public void addPermission(String node) {
        permissions.add(node);
    }

    @Override
    public void removePermission(String node) {
        permissions.remove(node);
    }

    @Override
    public List<String> getGroupPermission(String tag) {
        return groupPermissions.get(tag);
    }

    @Override
    public List<String> getOrPutGroupPermission(String tag) {
        List<String> groupPermission = getGroupPermission(tag);
        if (Objects.isNull(groupPermission)) {
            groupPermission = new ArrayList<>();
            groupPermissions.put(tag, groupPermission);
        }
        return groupPermission;
    }
}