package cn.codethink.xiaoming.code;

import cn.codethink.common.util.Numbers;
import cn.codethink.common.util.Preconditions;

import java.util.NoSuchElementException;

/**
 * 用户的唯一标识码
 *
 * @author Chuanwise
 */
public interface Code {
    
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
    static Code parseCode(String input) {
        Preconditions.objectArgumentNonEmpty(input, "input");
    
        final int delimiter = input.indexOf(':');
        Preconditions.argument(delimiter != -1, "code type required");
        Preconditions.argument(delimiter != input.length(), "code value required");
        
        final String type = input.substring(0, delimiter);
        final String value = input.substring(delimiter + 1);
    
        switch (type) {
            case "long":
            case "l":
                final Long longValue = Numbers.parseLong(value);
                Preconditions.nonNull(longValue);
                return LongCode.valueOf(longValue);
            case "string":
            case "str":
            case "s":
                return new StringCode(value);
            case "integer":
            case "int":
            case "i":
                final Integer intValue = Numbers.parseInt(value);
                Preconditions.nonNull(intValue);
                return IntCode.valueOf(intValue);
            default:
                throw new NoSuchElementException("unknown code type: " + type);
        }
    }
    
    /**
     * 获取码值
     *
     * @return 码值
     */
    Object getValue();
    
    /**
     * 将码转化为 Long 码
     *
     * @return long 码
     * @throws UnsupportedOperationException 当码并非 Long 码时
     */
    long asLong();
    
    /**
     * 将码转化为 int 码
     *
     * @return int 码
     * @throws UnsupportedOperationException 当码并非 int 码时
     */
    int asInt();
    
    /**
     * 将码转化为 String 码
     *
     * @return String 码
     * @throws UnsupportedOperationException 当码并非 String 码时
     */
    String asString();
}