package cn.chuanwise.toolkit.tag;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public interface TagMarkable {
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
        return addTags(Arrays.asList(tags));
    }

    default boolean addTags(Collection<String> collection) {
        boolean changed = false;
        for (String tag : collection) {
            final boolean thisTimeChanged = addTag(tag);
            changed = changed || thisTimeChanged;
        }
        return changed;
    }

    default boolean isOriginalTag(String tag) {
        return getOriginalTags().contains(tag);
    }

    Set<String> getOriginalTags();

    Set<String> getTags();

    default void flushTags() {
        getTags().addAll(getOriginalTags());
        getTags().add(RECORDED);
    }
}
