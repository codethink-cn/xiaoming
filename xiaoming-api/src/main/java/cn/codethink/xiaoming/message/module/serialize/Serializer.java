package cn.codethink.xiaoming.message.module.serialize;

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.Priority;
import cn.codethink.xiaoming.message.Serializable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

/**
 * <h1>序列化器</h1>
 *
 * <p>用于声明一个序列化方法，将对象序列化为字符数组 {@link String[]} 或 {@link List<String>}。</p>
 *
 * @author Chuanwise
 *
 * @see SerializeHandler
 * @see cn.codethink.xiaoming.message.module.MessageModule#serialize(Object, Map)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Serializer {
    
    /**
     * 序列化参数的类型
     *
     * @return 序列化参数的类型
     */
    Class<?> value();
    
    /**
     * 组件优先级
     *
     * @return 组件优先级
     */
    Priority priority() default Priority.NORMAL;
}
