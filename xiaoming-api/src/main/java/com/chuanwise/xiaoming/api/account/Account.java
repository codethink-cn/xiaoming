package com.chuanwise.xiaoming.api.account;

import com.chuanwise.xiaoming.api.preserve.Preservable;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface Account extends Preservable<File> {
    default void addEvent(AccountEvent event) {
        getEvents().add(event);
    }

    default void addHistory(AccountEvent history) {
        getHistories().add(history);
    }

    default void addCommand(AccountEvent command) {
        getCommands().add(command);
    }

    long getCode();

    String getAlias();

    List<AccountEvent> getEvents();

    List<AccountEvent> getCommands();

    List<AccountEvent> getHistories();

    void setCode(long code);

    void setAlias(String alias);

    default boolean isBlockPlugin(String pluginName) {
        return hasTag("plugin.block." + pluginName);
    }

    default void blockPlugin(String pluginName) {
        addTag("plugin.block." + pluginName);
    }

    default void unblockPlugin(String pluginName) {
        removeTag("plugin.block." + pluginName);
    }

    Set<String> getTags();

    default void addTag(String tag) {
        getTags().add(tag);
    }

    default void removeTag(String tag) {
        getTags().remove(tag);
    }

    default boolean hasTag(String tag) {
        return getTags().contains(tag) || tag == getCode() + "" || tag == "recorded";
    }
}
