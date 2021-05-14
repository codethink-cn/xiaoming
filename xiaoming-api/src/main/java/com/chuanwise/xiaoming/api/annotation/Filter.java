package com.chuanwise.xiaoming.api.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Fliters.class)
public @interface Filter {
    String value();

    FliterPattern pattern() default FliterPattern.PARAMETER;
}
