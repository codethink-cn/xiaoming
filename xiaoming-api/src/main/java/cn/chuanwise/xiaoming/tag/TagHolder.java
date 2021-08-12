package cn.chuanwise.xiaoming.tag;

import cn.chuanwise.xiaoming.plugin.XiaomingPlugin;

import java.util.Arrays;
import java.util.Set;

public interface TagHolder {
    String RECORDED = "recorded";

    default boolean hasTag(String tag) {
        return getTags().contains(tag);
    }

    default boolean removeTag(String tag) {
        if (Arrays.asList(RECORDED, getName()).contains(tag)) {
            return false;
        }
        return getTags().remove(tag);
    }

    default boolean addTag(String tag) {
        return getTags().add(tag);
    }

    Set<String> getTags();

    String getName();

    default void flushTags() {
        getTags().addAll(Arrays.asList(getName(), RECORDED));
    }
}
