package com.chuanwise.xiaoming.api.response;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public interface ResponseGroup {
    /**
     * 判断一个群内是否屏蔽了插件
     * @param pluginName 插件名
     * @return 是否屏蔽了本插件
     */
    boolean isBlockPlugin(String pluginName);

    /**
     * 判断某个群是否含有一个标记
     * @param tag
     * @return
     */
    default boolean hasTag(String tag) {
        return getTags().contains(tag) || Objects.equals(getCode() + "", tag) || Objects.equals(tag, "recorded");
    }

    long getCode();

    String getAlias();

    Set<String> getBlockedPlugins();

    Set<String> getTags();

    default void removeTag(String tag) {
        getTags().remove(tag);
    }

    default void addTag(String tag) {
        getTags().add(tag);
    }

    void setAlias(String alias);

    void blockPlugin(String pluginName);
}
