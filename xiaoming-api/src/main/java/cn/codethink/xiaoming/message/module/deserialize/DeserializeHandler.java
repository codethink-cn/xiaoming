package cn.codethink.xiaoming.message.module.deserialize;

import cn.codethink.xiaoming.Bot;

import java.util.List;
import java.util.Map;

/**
 * 反序列化器
 *
 * @author Chuanwise
 *
 * @see DeserializeContext
 * @see cn.codethink.xiaoming.message.module.MessageModule#deserialize(List, Map)
 */
public interface DeserializeHandler {
    
    /**
     * 将消息反序列化为某个对象
     *
     * @param context 消息
     * @return 反序列化
     * @throws NullPointerException context 为 null
     * @throws Exception 反序列化出现异常
     */
    Object invoke(DeserializeContext context) throws Exception;
}
