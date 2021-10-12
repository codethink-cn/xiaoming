package cn.chuanwise.xiaoming.util;

import cn.chuanwise.util.StaticUtil;
import cn.chuanwise.xiaoming.permission.PermissionAccessible;

import java.util.List;
import java.util.Objects;

public class PermissionUtil extends StaticUtil {
    /** 判断一个权限是否具有给定权限节点的权限 */
    public static PermissionAccessible accessible(String node, String give) {
        boolean isMinusNode = node.startsWith("-");
        if (node.endsWith("*")) {
            final String nodeContent = node.substring(isMinusNode ? 1 : 0, node.lastIndexOf("*"));
            return give.startsWith(nodeContent) ? (isMinusNode ? PermissionAccessible.UNACCESSABLE : PermissionAccessible.ACCESSABLE) : PermissionAccessible.UNKNOWN;
        } else {
            if (isMinusNode) {
                return Objects.equals(give, "-" + node) ? PermissionAccessible.UNACCESSABLE : PermissionAccessible.UNKNOWN;
            } else {
                return Objects.equals(give, node) ? PermissionAccessible.ACCESSABLE : PermissionAccessible.UNKNOWN;
            }
        }
    }

    /** 判断一个权限是否具有给定权限节点的权限 */
    public static PermissionAccessible accessible(List<String> permissions, String give) {
        for (String per : permissions) {
            final PermissionAccessible accessable = PermissionUtil.accessible(per, give);
            if (accessable != PermissionAccessible.UNKNOWN) {
                return accessable;
            }
        }
        return PermissionAccessible.UNKNOWN;
    }
}
