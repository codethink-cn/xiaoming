package com.chuanwise.xiaoming.api.word;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * 小明的提示文本管理器
 */
public interface WordManager extends Preservable<File>, XiaomingObject {
    Map<String, Set<String>> getValues();

    Set<String> getSet(String key);

    String get(String key);
}
