package com.chuanwise.xiaoming.api.util;

import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;

/**
 * 不能实例化的工具类的基类
 * @author Chuanwise
 */
public abstract class StaticUtils extends Utils {
    protected StaticUtils() {
        throw new XiaomingRuntimeException("不能构造静态工具类！");
    }
}
