package cn.codethink.xiaoming.message.parser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 基础消息解析器。是用于
 *
 * @author Chuanwise
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BasicMessageParser {
    
    /**
     * 参数列表
     *
     * @return 参数列表
     */
    String[] value();
}
