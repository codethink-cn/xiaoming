package com.chuanwise.xiaoming.core.account;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.account.AccountManager;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.Friend;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class AccountManagerImpl extends HostObjectImpl implements AccountManager {
    File directory;

    Map<Long, Account> loadedAccounts = new ConcurrentHashMap<>();

    public AccountManagerImpl(XiaomingBot xiaomingBot, File directory) {
        super(xiaomingBot);
        this.directory = directory;
    }

    @Override
    public File accountFile(long qq) {
        return new File(directory, qq + ".json");
    }

    @Override
    public Account getAccount(long qq) {
        Account account = loadedAccounts.get(qq);
        if (Objects.nonNull(account)) {
            return account;
        }
        account = getXiaomingBot().getFilePreservableFactory().load(AccountImpl.class, accountFile(qq));
        if (Objects.nonNull(account)) {
            loadedAccounts.put(qq, account);
        }
        return account;
    }

    @Override
    public Account getOrPutAccount(long qq) {
        Account account = getAccount(qq);
        if (Objects.isNull(account)) {
            account = new AccountImpl();
            account.setMedium(accountFile(qq));
            account.setQq(qq);
            final Friend friend = getXiaomingBot().getMiraiBot().getFriend(qq);
            if (Objects.nonNull(friend)) {
                account.setAlias(friend.getNick());
            }
            loadedAccounts.put(qq, account);
        }
        return account;
    }
}
