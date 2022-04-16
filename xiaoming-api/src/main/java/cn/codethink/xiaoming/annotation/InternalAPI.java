package cn.codethink.xiaoming.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 使用该注解的，都应该只由小明内部使用。
 *
 * 可能会在没有任何提示的情况下被任意修改，
 * 非常不建议在软件中使用。
 *
 * @author Chuanwise
 */
@Documented
@Retention(RetentionPolicy.CLASS)
public @interface InternalAPI {
}
