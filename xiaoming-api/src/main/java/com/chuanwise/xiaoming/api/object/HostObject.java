package com.chuanwise.xiaoming.api.object;

import org.slf4j.Logger;

/**
 * 小明本体对象
 */
public interface HostObject extends XiaomingObject {
    /**
     * 获取当前对象的日志
     * @return 日志对象
     */
    Logger getLog();
}
