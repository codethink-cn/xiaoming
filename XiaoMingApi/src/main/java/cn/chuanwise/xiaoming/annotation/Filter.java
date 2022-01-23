package cn.chuanwise.xiaoming.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Filters.class)
public @interface Filter {
    String value();

    FilterPattern pattern() default FilterPattern.PARAMETER;
}
