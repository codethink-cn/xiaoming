package cn.chuanwise.xiaoming.account;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.StringUtil;
import cn.chuanwise.xiaoming.account.record.CommandRecord;
import cn.chuanwise.xiaoming.account.record.Record;
import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.xiaoming.tag.PluginBlockable;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public interface Account extends Preservable<File>, PluginBlockable {
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

    default String getAliasAndCode() {
        final String alias = getAlias();
        return Objects.nonNull(alias) ? (alias + "（" + getCodeString() + "）") : getCodeString();
    }

    default String getAliasOrCode() {
        return StringUtil.firstNonEmpty(getAlias(), getCodeString());
    }

    String getAlias();

    List<Record> getEvents();

    List<CommandRecord> getCommands();

    List<Record> getHistories();

    void setCode(long code);

    void setAlias(String alias);

    @Override
    default Set<String> getOriginalTags() {
        return CollectionUtil.asSet(getCodeString(), RECORDED);
    }
}
