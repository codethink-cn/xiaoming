package com.chuanwise.xiaoming.api.language;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.toolkit.preservable.Preservable;

import java.io.File;
import java.util.Map;

/**
 * 小明的提示文本管理器
 * @author Chuanwise
 */
public interface Language extends Preservable<File>, XiaomingObject {
    default String getString(String key) {
        return getStringOrDefault(key, key);
    }

    String getStringOrDefault(String key, String onFail);

    Map<String, Object> getValues();

    default Object get(String key) {
        return getValues().get(key);
    }

    default void put(String key, Object value) {
        getValues().put(key, value);
    }
}