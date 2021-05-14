package com.chuanwise.xiaoming.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 群聊交互器注解
 * 当其注解在类上时，类内的所有交互器方法自动只在群聊时生效
 * @author Chuanwise
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GroupInteractor {
    /**
     * 交互所在群聊
     */
    long value() default 0;

    /**
     * 对方 QQ
     */
    long qq() default 0;
}