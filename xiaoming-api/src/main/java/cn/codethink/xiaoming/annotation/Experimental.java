package cn.codethink.xiaoming.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 使用该注解的，都是实验性的。可能会在未来被移除，或者被修改含义。
 *
 * 非常不建议在软件的正式版中使用。
 *
 * @author Chuanwise
 */
@Retention(RetentionPolicy.CLASS)
public @interface Experimental {
}
