package com.chuanwise.xiaoming.api.annotation;

import java.lang.annotation.*;
import java.util.Set;
import java.util.function.Function;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RequirePermissions.class)
public @interface RequirePermission {
    String value();
}
