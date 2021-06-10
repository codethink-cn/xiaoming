package com.chuanwise.xiaoming.api.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Requires.class)
public @interface Require {
    String value();
}
