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

    long getQq();

    String getAlias();

    List<AccountEvent> getEvents();

    List<AccountEvent> getCommands();

    List<AccountEvent> getHistories();

    void setQq(long qq);

    void setAlias(String alias);

    void setEvents(List<AccountEvent> events);

    void setHistories(List<AccountEvent> histories);

    boolean isBlockPlugin(String pluginName);

    void blockPlugin(String pluginName);

    Set<String> getBlockPlugins();
}
