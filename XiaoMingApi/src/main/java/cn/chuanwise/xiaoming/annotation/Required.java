package cn.chuanwise.xiaoming.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Permissions.class)
public @interface Required {
    String value();
}
