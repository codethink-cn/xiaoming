package cn.chuanwise.xiaoming.permission;

import cn.chuanwise.xiaoming.permission.PermissionUserNode;
import lombok.Data;

import java.util.*;

@Data
public class PermissionUserNodeImpl implements PermissionUserNode {
    String group;

    List<String> permissions = new ArrayList<>();
    Map<String, List<String>> groupPermissions = new HashMap<>();

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
