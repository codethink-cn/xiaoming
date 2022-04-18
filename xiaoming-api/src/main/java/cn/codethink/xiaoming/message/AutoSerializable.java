package cn.codethink.xiaoming.message;

import cn.chuanwise.common.util.Collections;
import cn.codethink.xiaoming.message.module.MessageModule;
import cn.codethink.xiaoming.property.Property;

import java.util.Map;

/**
 * <h1>可自动序列化对象</h1>
 *
 * <p>该接口是为已注册序列化组件的类型编写的，自动调用 {@link MessageModule#serialize(Object, Map)} 序列化为消息码。</p>
 *
 * <p>请确保在调用 {@link #serializeToMessageCode(Map)} 前，已经通过 {@link MessageModule#registerModule(Object)}
 * 注册了序列化器。否则该方法将抛出 {@link IllegalArgumentException}异常。</p>
 *
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.Summarizable
 * @see cn.codethink.xiaoming.message.module.summary.Summarizer
 */
public interface AutoSerializable
    extends Serializable {
    
    /**
     * 按照注册的消息组件序列化为消息码
     *
     * @param properties 相关属性
     * @return 消息码
     * @throws NullPointerException     properties 为 null
     * @throws IllegalArgumentException 没有合适的序列化器
     */
    @Override
    default String serializeToMessageCode(Map<Property<?>, Object> properties) {
        return "[" + Collections.toString(MessageModule.serialize(this, properties), ":") + "]";
    }
}