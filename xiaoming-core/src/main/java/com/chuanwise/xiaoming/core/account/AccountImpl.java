package com.chuanwise.xiaoming.core.account;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.account.AccountEvent;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
public class AccountImpl extends JsonFilePreservable implements Account {
    long code;
    String alias;

    List<AccountEventImpl> events = new ArrayList<>();
    List<AccountEventImpl> histories = new ArrayList<>();
    List<AccountEventImpl> commands = new ArrayList<>();

    Set<String> tags = new HashSet<>();

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    @Override
    public List<AccountEvent> getHistories() {
        return (List) histories;
    }

    @Override
    public List<AccountEvent> getEvents() {
        return (List) events;
    }

    @Override
    public List<AccountEvent> getCommands() {
        return (List) commands;
    }

    public AccountImpl(long code, String alias) {
        this.setCode(code);
        setAlias(alias);
    }
}
