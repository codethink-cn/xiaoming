package cn.codethink.xiaoming.message.module.deserialize;

import cn.codethink.xiaoming.Priority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

/**
 * <h1>反序列化器</h1>
 *
 * <p>用于声明一个反序列化方法，该方法将字符串数组 {@link String[]} 或列表 {@link java.util.List<String>} 反序列化为一个对象。</p>
 *
 * @author Chuanwise
 *
 * @see DeserializeHandler
 * @see cn.codethink.xiaoming.message.module.MessageModule#deserialize(List, Map)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Deserializer {
    
    /**
     * <h1>反序列化数组格式。</h1>
     *
     * <p>由 : 和非 : 两种符号组成。可以使用 \: 转义。: 将数组划分为一个个的部分。</p>
     *
     * <p>每个部分，用 ? 表示一个参数、?? 表示零个或多个参数，?! 表示一个或多个参数，其他表示原文。</p>
     *
     * <p>例如，"image:url:?!" 表示 url 图片，后面至少需要一个参数。"flash:?!" 表示一张闪照。</p>
     *
     * @return 数组格式
     */
    String value();
    
    /**
     * 组件优先级
     *
     * @return 组件优先级
     */
    Priority priority() default Priority.NORMAL;
}
