package com.chuanwise.xiaoming.api.account;

import com.chuanwise.xiaoming.api.account.record.CommandRecord;
import com.chuanwise.xiaoming.api.account.record.Record;
import com.chuanwise.toolkit.preservable.Preservable;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public interface Account extends Preservable<File> {
    default void addEvent(Record event) {
        getEvents().add(event);
    }

    default void addHistory(Record history) {
        getHistories().add(history);
    }

    default void addCommand(CommandRecord command) {
        getCommands().add(command);
    }

    long getCode();

    default String getCodeString() {
        return String.valueOf(getCode());
    }

    default String getCompleteName() {
        final String alias = getAlias();
        return Objects.nonNull(alias) ? (alias + "（" + getCodeString() + "）") : getCodeString();
    }

    String getAlias();

    List<Record> getEvents();

    List<CommandRecord> getCommands();

    List<Record> getHistories();

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
        return getTags().contains(tag);
    }
}
