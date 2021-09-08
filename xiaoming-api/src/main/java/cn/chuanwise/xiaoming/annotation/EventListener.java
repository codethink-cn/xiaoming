package cn.chuanwise.xiaoming.annotation;

import cn.chuanwise.xiaoming.listener.ListenerPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 小明消息响应方法注解
 * @author Chuanwise
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {

    ListenerPriority priority() default ListenerPriority.NORMAL;
    
    boolean ignoreCancelled() default true;
}