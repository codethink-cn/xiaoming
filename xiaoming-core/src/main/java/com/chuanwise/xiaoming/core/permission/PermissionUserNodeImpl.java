package com.chuanwise.xiaoming.core.permission;

import com.chuanwise.xiaoming.api.account.record.Record;
import com.chuanwise.xiaoming.api.permission.PermissionUserNode;
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
