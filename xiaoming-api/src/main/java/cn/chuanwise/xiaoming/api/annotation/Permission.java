package cn.chuanwise.xiaoming.api.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Permissions.class)
public @interface Permission {
    String value();
}
