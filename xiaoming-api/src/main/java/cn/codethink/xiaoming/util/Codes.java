package cn.codethink.xiaoming.util;

import cn.codethink.common.util.Preconditions;
import cn.codethink.common.util.StaticUtilities;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.code.LongCode;

/**
 * 标识工具
 *
 * @author Chuanwise
 */
public class Codes
        extends StaticUtilities {
    
    /**
     * 检查是否是 Long Code
     *
     * @param code 码
     */
    public static void requiredLongCode(Code code) {
        Preconditions.namedArgumentNonNull(code, "code");
        Preconditions.argument(code instanceof LongCode, "code should be long code");
    }
}
