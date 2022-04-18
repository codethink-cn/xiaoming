package cn.codethink.xiaoming.message.module.deserialize;

import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.property.PropertyHolder;

import java.util.List;
import java.util.Map;

/**
 * 反序列化上下文
 *
 * @author Chuanwise
 *
 * @see DeserializeHandler
 * @see cn.codethink.xiaoming.message.module.MessageModule#deserialize(List, Map)
 */
public interface DeserializeContext
    extends BotObject, PropertyHolder {
    
    /**
     * 获取反序列化的参数
     *
     * @return 反序列化的参数
     */
    List<String> getArguments();
}
