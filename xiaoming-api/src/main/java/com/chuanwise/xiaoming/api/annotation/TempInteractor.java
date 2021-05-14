package com.chuanwise.xiaoming.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 临时消息交互器注解
 * 当其注解在类上时，类内的所有交互器方法自动只在临时消息时生效
 * @author Chuanwise
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TempInteractor {
    /**
     * 对方来自哪个群
     */
    long value() default 0;

    /**
     * 对方 QQ
     */
    long qq() default 0;
}
