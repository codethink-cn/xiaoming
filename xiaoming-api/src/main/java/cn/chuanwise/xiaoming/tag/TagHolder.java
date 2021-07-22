package cn.chuanwise.xiaoming.tag;

import cn.chuanwise.xiaoming.plugin.XiaomingPlugin;

import java.util.Set;

public interface TagHolder {
    default boolean hasTag(String tag) {
        return getTags().contains(tag);
    }

    default void removeTag(String tag) {
        getTags().remove(tag);
    }

    default void addTag(String tag) {
        getTags().add(tag);
    }

    Set<String> getTags();

    default boolean isBlockPlugin(XiaomingPlugin plugin) {
        return hasTag(plugin.getXiaomingBot().getConfiguration().getBlockPluginTagPrefix() + plugin.getName());
    }

    default void blockPlugin(XiaomingPlugin plugin) {
        addTag(plugin.getXiaomingBot().getConfiguration().getBlockPluginTagPrefix() + plugin.getName());
    }

    default void unblockPlugin(XiaomingPlugin plugin) {
        removeTag(plugin.getXiaomingBot().getConfiguration().getBlockPluginTagPrefix() + plugin.getName());
    }
}
