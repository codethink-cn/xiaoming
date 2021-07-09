package com.chuanwise.xiaoming.core.account;

import com.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.account.record.CommandRecord;
import com.chuanwise.xiaoming.api.account.record.Record;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.Supplier;

@Data
@NoArgsConstructor
public class AccountImpl extends FilePreservableImpl implements Account {
    long code;
    String alias;

    List<Record> events = new ArrayList<>();
    List<Record> histories = new ArrayList<>();
    List<CommandRecord> commands = new ArrayList<>();

    Set<String> tags = new HashSet<>();

    public void setTags(Set<String> tags) {
        this.tags = tags;
        tags.addAll(Arrays.asList(String.valueOf(code), "recorded"));
    }
}
