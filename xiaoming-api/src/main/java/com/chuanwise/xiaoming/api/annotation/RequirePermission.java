package com.chuanwise.xiaoming.api.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RequirePermissions.class)
public @interface RequirePermission {
    String value();
}
