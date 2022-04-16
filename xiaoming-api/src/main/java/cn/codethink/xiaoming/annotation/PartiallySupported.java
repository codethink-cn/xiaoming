package cn.codethink.xiaoming.annotation;

import cn.codethink.xiaoming.IM;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 具备该注解的方法或属性，只有部分通讯软件实现了。
 *
 * @author Chuanwise
 */
@Documented
@Retention(RetentionPolicy.CLASS)
public @interface PartiallySupported {
    
    /**
     * 获取即时通讯软件列表
     *
     * @return 即时通讯软件列表
     */
    IM[] value();
}
