package com.chuanwise.xiaoming.core.account;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.account.AccountEvent;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Data
@NoArgsConstructor
public class AccountImpl extends JsonFilePreservable implements Account {
    long qq;
    String alias;
    Set<String> blockPlugins = new CopyOnWriteArraySet<>();

    List<AccountEvent> events = new ArrayList<>();
    List<AccountEvent> histories = new ArrayList<>();
    List<AccountEvent> commands = new ArrayList<>();

    public AccountImpl(long qq, String alias) {
        setQq(qq);
        setAlias(alias);
    }

    @Override
    public boolean isBlockPlugin(String pluginName) {
        return blockPlugins.contains(pluginName);
    }

    @Override
    public void blockPlugin(String pluginName) {
        blockPlugins.add(pluginName);
    }
}
