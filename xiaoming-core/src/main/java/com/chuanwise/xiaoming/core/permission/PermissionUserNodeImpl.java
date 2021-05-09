package com.chuanwise.xiaoming.core.permission;

import com.chuanwise.xiaoming.api.permission.PermissionUserNode;
import com.chuanwise.xiaoming.api.util.PermissionUtil;
import com.chuanwise.xiaoming.core.object.XiaomingObjectImpl;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class PermissionUserNodeImpl implements PermissionUserNode {
    String group;

    List<String> permissions = new ArrayList<>();

    @Override
    public void addPermission(String node) {
        permissions.add(node);
    }

    @Override
    public boolean hasPrivatePermission(String node) {
        if (Objects.isNull(permissions)) {
            return false;
        }
        for (String per : permissions) {
            final int accessable = PermissionUtil.accessable(per, node);
            if (accessable == 0) {
                continue;
            } else {
                return accessable > 0;
            }
        }
        return false;
    }
}
