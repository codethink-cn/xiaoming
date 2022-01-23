package cn.chuanwise.xiaoming.util;

import cn.chuanwise.util.ConditionUtil;
import cn.chuanwise.util.StaticUtil;

import java.util.Objects;

public class PermissionUtil extends StaticUtil {
    public static boolean isAccessible(String owned, String required) {
        ConditionUtil.notNull(owned, "owned permission");
        ConditionUtil.notNull(required, "required permission");

        final int spiltter = owned.lastIndexOf("*");
        if (spiltter == -1) {
            return Objects.equals(owned, required);
        } else {
            return required.startsWith(owned.substring(0, spiltter));
        }
    }
}
