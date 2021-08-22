package cn.chuanwise.xiaoming.tag;

import java.util.Arrays;
import java.util.Set;

public interface TagHolder {
    String RECORDED = "recorded";

    default boolean hasTag(String tag) {
        return getTags().contains(tag);
    }

    default boolean hasTags(String... tags) {
        for (String tag : tags) {
            if (!hasTag(tag)) {
                return false;
            }
        }
        return true;
    }

    default boolean removeTag(String tag) {
        if (isOriginalTag(tag)) {
            return false;
        }
        return getTags().remove(tag);
    }

    default boolean addTag(String tag) {
        return getTags().add(tag);
    }

    default boolean addTags(String... tags) {
        for (String tag : tags) {
            if (!addTag(tag)) {
                return false;
            }
        }
        return true;
    }

    default boolean isOriginalTag(String tag) {
        return buildOriginalTags().contains(tag);
    }

    Set<String> buildOriginalTags();

    Set<String> getTags();

    default void flushTags() {
        getTags().addAll(buildOriginalTags());
        getTags().add(RECORDED);
    }
}
