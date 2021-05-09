package com.chuanwise.xiaoming.api.util;

public class PermissionUtil {
    /**
     * 判断一个权限是否具有给定权限节点的权限
     * @return 1 表示有权限，0 表示未知，-1 表示无权限
     */
    public static int accessable(String node, String give) {
        if (node.equals(give)) {
            return 1;
        } else if (node.equals("-" + give)) {
            return -1;
        } else if (node.endsWith("*") && give.startsWith(node.substring(0, node.lastIndexOf("*")))) {
            return 1;
        }
        return 0;
    }
}
