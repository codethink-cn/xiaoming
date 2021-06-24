package com.chuanwise.xiaoming.api.util;

import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;

/**
 * 只能实例化一次的工具类的基类
 * @author Chuanwise
 */
public abstract class UnstaticUtils extends Utils {
    private boolean constructed = false;

    protected UnstaticUtils() {
        if (constructed) {
            throw new XiaomingRuntimeException("非静态工具类只能构造一次！");
        } else {
            constructed = true;
        }
    }
}
