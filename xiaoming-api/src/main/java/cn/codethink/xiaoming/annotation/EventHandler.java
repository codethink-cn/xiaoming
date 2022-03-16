package cn.codethink.xiaoming.annotation;

import cn.codethink.xiaoming.Priority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 事件监听器方法
 *
 * @author Chuanwise
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
    
    /**
     * 监听器优先级
     *
     * @return 监听器优先级
     */
    Priority priority() default Priority.NORMAL;
    
    /**
     * 如果事件被取消，是否仍然监听
     *
     * @return 是否仍然监听
     */
    boolean alwaysValid() default false;
}
