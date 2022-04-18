package cn.codethink.xiaoming.message.module.convert;

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.Priority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * <h1>转换器</h1>
 *
 * <p>用于声明一个消息转换器</p>
 *
 * @author Chuanwise
 *
 * @see ConvertHandler
 * @see cn.codethink.xiaoming.message.module.MessageModule#convert(Object, Class, Map)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Convertor {
    
    /**
     * 转换参数的类型
     *
     * @return 转换参数的类型
     */
    Class<?> value();
    
    /**
     * 组件优先级
     *
     * @return 组件优先级
     */
    Priority priority() default Priority.NORMAL;
    
    /**
     * 转换目标，空表示方法返回值就是转换目标
     *
     * @return 转换目标
     */
    Class<?>[] targets() default {};
}
