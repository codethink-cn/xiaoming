package cn.codethink.xiaoming.code;

import cn.codethink.xiaoming.message.Serializable;
import cn.codethink.util.Numbers;
import cn.codethink.util.Preconditions;

import java.util.NoSuchElementException;

/**
 * 用户的唯一标识码
 *
 * @author Chuanwise
 */
public interface Code
        extends Serializable {
    
    /**
     * 创建一个 LongCode
     *
     * @param code 值
     * @return 码
     */
    static Code ofLong(long code) {
        return LongCode.valueOf(code);
    }
    
    /**
     * 创建一个 LongCode
     *
     * @param code 值
     * @return 码
     */
    static Code ofInt(int code) {
        return IntCode.valueOf(code);
    }
    
    /**
     * 创建一个 StringCode
     *
     * @param code 值
     * @return 码
     */
    static Code ofString(String code) {
        return new StringCode(code);
    }
    
    /**
     * 反序列化标识码
     *
     * @param input 标识码字符串
     * @return 标识码
     */
    static Code deserialize(String input) {
        Preconditions.namedArgumentNonEmpty(input, "input");
    
        final int delimiter = input.indexOf(':');
        Preconditions.argument(delimiter != -1, "编译错误：没有找到码类型");
        Preconditions.argument(delimiter != input.length(), "编译错误：无码值");
        
        final String type = input.substring(0, delimiter);
        final String value = input.substring(delimiter + 1);
    
        switch (type) {
            case "long":
            case "l":
                return LongCode.valueOf(Numbers.parseLong(value).orElseThrow(IllegalArgumentException::new));
            case "string":
            case "str":
            case "s":
                return new StringCode(value);
            case "integer":
            case "int":
            case "i":
                return IntCode.valueOf(Numbers.parseInt(value).orElseThrow(IllegalArgumentException::new));
            default:
                throw new NoSuchElementException("未知代码类型：" + type);
        }
    }
}
