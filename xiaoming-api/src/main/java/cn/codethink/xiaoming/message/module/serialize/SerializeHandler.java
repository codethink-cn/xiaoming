package cn.codethink.xiaoming.message.module.serialize;

import cn.codethink.xiaoming.Bot;

import java.util.List;
import java.util.Map;

/**
 * 序列化器
 *
 * @author Chuanwise
 *
 * @see SerializeContext
 * @see cn.codethink.xiaoming.message.module.MessageModule#serialize(Object, Map)
 */
public interface SerializeHandler {
    
    /**
     * 将对象序列化为参数列表
     *
     * @param context 序列化上下文
     * @return 参数列表
     * @throws NullPointerException context 为 null
     * @throws Exception 序列化出现异常
     */
    List<String> serialize(SerializeContext context) throws Exception;
}
