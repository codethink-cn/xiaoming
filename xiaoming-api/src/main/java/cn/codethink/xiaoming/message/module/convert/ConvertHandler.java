package cn.codethink.xiaoming.message.module.convert;

import cn.codethink.xiaoming.Bot;

import java.util.Map;

/**
 * <h1>转换器</h1>
 *
 * <p>用于在小明基础消息或元数据转换为其他平台的消息或元数据。</p>
 *
 * @author Chuanwise
 *
 * @see ConvertContext
 * @see cn.codethink.xiaoming.message.module.MessageModule#convert(Object, Class, Map)
 */
public interface ConvertHandler {
    
    /**
     * 将一个对象转化为另一种对象
     *
     * @param context 转换上下文
     * @return 目标对象
     * @throws NullPointerException context 为 null
     * @throws Exception 转换出现异常
     */
    Object convert(ConvertContext context) throws Exception;
}
