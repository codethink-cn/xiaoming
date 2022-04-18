package cn.codethink.xiaoming.util;

import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.spi.XiaoMing;

import java.util.Collections;
import java.util.Map;

/**
 * 消息码相关工具
 *
 * @author Chuanwise
 */
public interface MessageCode {
    
    /**
     * 反序列化消息码为消息体
     *
     * @param messageCode 消息码
     * @param properties  相关属性
     * @return 消息体
     * @throws NullPointerException     messageCode 或 properties 为 null
     * @throws IllegalArgumentException messageCode 为 ""
     */
    static CompoundMessage deserializeMessageCode(String messageCode, Map<Property<?>, Object> properties) {
        return XiaoMing.get().deserializeMessageCode(messageCode, properties);
    }
    
    /**
     * 反序列化消息码为消息体
     *
     * @param messageCode 消息码
     * @return 消息体
     * @throws NullPointerException     messageCode 为 null
     * @throws IllegalArgumentException messageCode 为 ""
     */
    static CompoundMessage deserializeMessageCode(String messageCode) {
        return XiaoMing.get().deserializeMessageCode(messageCode, Collections.emptyMap());
    }
}