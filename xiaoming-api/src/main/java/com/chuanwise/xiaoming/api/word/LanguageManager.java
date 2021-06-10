package com.chuanwise.xiaoming.api.word;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * 小明的提示文本管理器
 * @author Chuanwise
 */
public interface LanguageManager extends Preservable<File>, XiaomingObject {
    String getString(String key);

    Map<String, Object> getValues();

    default Object get(String key) {
        return getValues().get(key);
    }

    default void put(String key, Object value) {
        getValues().put(key, value);
    }
}