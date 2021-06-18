package com.chuanwise.xiaoming.core.account;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.account.record.CommandRecord;
import com.chuanwise.xiaoming.api.account.record.Record;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
public class AccountImpl extends JsonFilePreservable implements Account {
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
