package cn.chuanwise.xiaoming.annotation;

import java.lang.annotation.*;

@Repeatable(RequireGroupTags.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireGroupTag {
    String value();
}
