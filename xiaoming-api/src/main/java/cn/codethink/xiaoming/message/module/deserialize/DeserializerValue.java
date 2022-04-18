package cn.codethink.xiaoming.message.module.deserialize;

import cn.codethink.xiaoming.Bot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

/**
 * 基础消息反序列化器参数。
 *
 * @author Chuanwise
 *
 * @see DeserializeHandler
 * @see cn.codethink.xiaoming.message.module.MessageModule#deserialize(List, Map)
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeserializerValue {
    
    /**
     * 参数的索引，-1 表示自动计算。
     *
     * @return 参数的索引或 -1
     */
    int value() default -1;
}
