package cn.codethink.xiaoming.message.module.serialize;

import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.property.PropertyHolder;

import java.util.Map;

/**
 * 序列化上下文
 *
 * @author Chuanwise
 *
 * @see SerializeHandler
 * @see cn.codethink.xiaoming.message.module.MessageModule#serialize(Object, Map)
 */
public interface SerializeContext
    extends BotObject, PropertyHolder {
    
    /**
     * 获取要序列化的对象
     *
     * @return 要序列化的对象
     */
    Object getSource();
}
