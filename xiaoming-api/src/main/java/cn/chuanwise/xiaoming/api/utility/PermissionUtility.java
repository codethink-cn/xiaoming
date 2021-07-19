package cn.chuanwise.xiaoming.api.utility;

import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.ObjectUtility;
import cn.chuanwise.utility.StaticUtility;
import cn.chuanwise.xiaoming.api.permission.PermissionAccessible;
import cn.chuanwise.xiaoming.api.permission.PermissionGroup;

import java.util.List;
import java.util.Objects;

public class PermissionUtility extends StaticUtility {
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
            final PermissionAccessible accessable = PermissionUtility.accessible(per, give);
            if (accessable != PermissionAccessible.UNKNOWN) {
                return accessable;
            }
        }
        return PermissionAccessible.UNKNOWN;
    }
}
