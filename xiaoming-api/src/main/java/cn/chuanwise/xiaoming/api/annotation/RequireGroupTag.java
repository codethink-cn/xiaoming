package cn.chuanwise.xiaoming.api.annotation;

import java.lang.annotation.*;

@Repeatable(RequireGroupTags.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireGroupTag {
    String value();
}
