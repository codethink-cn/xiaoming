package com.chuanwise.xiaoming.api.util;

import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;

public class UnstaticUtils {
    private static boolean constructed = false;

    protected UnstaticUtils() {
        if (constructed) {
            throw new XiaomingRuntimeException("Can not construct a util class multiplily.");
        } else {
            constructed = true;
        }
    }
}
