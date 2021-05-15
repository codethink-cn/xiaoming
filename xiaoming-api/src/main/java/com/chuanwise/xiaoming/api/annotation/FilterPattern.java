package com.chuanwise.xiaoming.api.annotation;

/**
 * @author Chuanwise
 */
public enum FilterPattern {
    EQUALS,
    STARTS_WITH,
    ENDS_WITH,
    STARTS_REGEX,
    ENDS_REGEX,
    EQUALS_IGNORE_CASE,
    MATCHES,
    PARAMETER,
}