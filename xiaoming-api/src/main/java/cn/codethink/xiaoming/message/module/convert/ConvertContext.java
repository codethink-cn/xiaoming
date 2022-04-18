package cn.codethink.xiaoming.message.module.convert;

import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.property.PropertyHolder;

import java.util.Map;

/**
 * 转换上下文
 *
 * @author Chuanwise
 *
 * @see ConvertHandler
 * @see cn.codethink.xiaoming.message.module.MessageModule#convert(Object, Class, Map)
 */
public interface ConvertContext
    extends BotObject, PropertyHolder {
    
    /**
     * 获取需要转换的对象
     *
     * @return 需要转换的对象
     */
    Object getSource();
    
    /**
     * 获取转换目标类型
     *
     * @return 目标类型
     */
    Class<?> getTargetClass();
}