package com.chuanwise.xiaoming.api.response;

import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public interface ResponseGroup extends XiaomingObject {
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
        return getTags().contains(tag);
    }

    long getCode();

    default String getCodeString() {
        return String.valueOf(getCode());
    }

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

    default String getCompleteName() {
        final String alias = getAlias();
        if (StringUtils.isEmpty(alias)) {
            return getCodeString();
        } else {
            return alias + "（" + getCodeString() + "）";
        }
    }

    default GroupContact getContact() {
        return getXiaomingBot().getContactManager().getGroupContact(getCode());
    }
}