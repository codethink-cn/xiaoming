package cn.chuanwise.xiaoming.tag;

import cn.chuanwise.xiaoming.plugin.Plugin;

import java.util.Objects;

public interface PluginBlockable extends TagHolder {
    default boolean isBlockPlugin(Plugin plugin) {
        if (Objects.isNull(plugin)) {
            return false;
        }
        return hasTag(plugin.getXiaomingBot().getConfiguration().getBlockPluginTagPrefix() + plugin.getName());
    }

    default void blockPlugin(Plugin plugin) {
        addTag(plugin.getXiaomingBot().getConfiguration().getBlockPluginTagPrefix() + plugin.getName());
    }

    default void unblockPlugin(Plugin plugin) {
        removeTag(plugin.getXiaomingBot().getConfiguration().getBlockPluginTagPrefix() + plugin.getName());
    }
}
