package cn.codethink.xiaoming.code;

import cn.codethink.common.util.Numbers;
import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.spi.XiaoMing;

import java.util.NoSuchElementException;

/**
 * 用户的唯一标识码
 *
 * @author Chuanwise
 */
public interface Code {
    
    /**
     * 获得一个代表 long 值的 Code
     *
     * @param code 值
     * @return 码
     */
    static Code ofLong(long code) {
        return XiaoMing.get().getCode(code);
    }
    
    /**
     * 获得一个代表 int 值的 Code
     *
     * @param code 值
     * @return 码
     */
    static Code ofInt(int code) {
        return XiaoMing.get().getCode(code);
    }
    
    /**
     * 获得一个代表字符串的 Code
     *
     * @param string 值
     * @return 码
     * @throws NullPointerException string 为 null
     */
    static Code ofString(String string) {
        return XiaoMing.get().getCode(string);
    }
    
    /**
     * 反序列化标识码
     *
     * @param string 标识码字符串
     * @return 标识码
     * @throws NullPointerException string 为 null
     * @throws IllegalArgumentException string 格式错误
     */
    static Code parseCode(String string) {
        return XiaoMing.get().parseCode(string);
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