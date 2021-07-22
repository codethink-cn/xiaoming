package cn.chuanwise.xiaoming.annotation;

import java.lang.annotation.*;

@Repeatable(RequireAccountTags.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAccountTag {
    String value();
}
