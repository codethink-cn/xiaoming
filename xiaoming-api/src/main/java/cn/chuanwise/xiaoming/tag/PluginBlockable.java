package cn.chuanwise.xiaoming.tag;

import cn.chuanwise.xiaoming.plugin.XiaomingPlugin;

public interface PluginBlockable extends TagHolder {
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
