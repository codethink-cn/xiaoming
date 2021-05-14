package com.chuanwise.xiaoming.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 私聊交互器注解
 * 当其注解在类上时，类内的所有交互器方法自动只在私聊时生效，所有
 * @author Chuanwise
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PrivateInteractor {
    /**
     * 对方 QQ
     */
    long value() default 0;
}
