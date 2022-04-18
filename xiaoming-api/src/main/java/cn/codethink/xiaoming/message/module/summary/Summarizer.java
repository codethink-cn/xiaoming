package cn.codethink.xiaoming.message.module.summary;

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.Priority;
import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.message.module.serialize.SerializeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

/**
 * <h1>摘要器</h1>
 *
 * <p>用于产生消息的摘要</p>
 *
 * @author Chuanwise
 *
 * @see SerializeHandler
 * @see cn.codethink.xiaoming.message.module.MessageModule#summary(AutoSummarizable, Map)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Summarizer {
    
    /**
     * 序列化参数的类型
     *
     * @return 序列化参数的类型
     */
    Class<? extends AutoSummarizable> value();
    
    /**
     * 组件优先级
     *
     * @return 组件优先级
     */
    Priority priority() default Priority.NORMAL;
}
