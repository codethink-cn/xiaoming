package com.chuanwise.xiaoming.api.util;

import com.chuanwise.xiaoming.api.permission.PermissionAccessible;

import java.util.List;

public class PermissionUtils {
    /**
     * 判断一个权限是否具有给定权限节点的权限
     * @return 1 表示有权限，0 表示未知，-1 表示无权限
     */
    public static PermissionAccessible accessible(String node, String give) {
        if (node.equals(give) || node.endsWith("*") && give.startsWith(node.substring(0, node.lastIndexOf("*")))) {
            return PermissionAccessible.ACCESSABLE;
        } else if (node.equals("-" + give)) {
            return PermissionAccessible.UNACCESSABLE;
        } else {
            return PermissionAccessible.UNKNOWN;
        }
    }

    /**
     * 判断一个权限是否具有给定权限节点的权限
     * @return 1 表示有权限，0 表示未知，-1 表示无权限
     */
    public static PermissionAccessible accessible(List<String> permissions, String give) {
        for (String per : permissions) {
            final PermissionAccessible accessable = PermissionUtils.accessible(per, give);
            if (accessable != PermissionAccessible.UNKNOWN) {
                return accessable;
            }
        }
        return PermissionAccessible.UNKNOWN;
    }
}
