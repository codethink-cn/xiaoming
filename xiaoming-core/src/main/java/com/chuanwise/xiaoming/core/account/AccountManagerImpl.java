package com.chuanwise.xiaoming.core.account;

import com.chuanwise.toolkit.preservable.file.FileLoader;
import com.chuanwise.utility.CollectionUtility;
import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.account.AccountManager;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.core.object.ModuleObjectImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.Friend;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class AccountManagerImpl extends ModuleObjectImpl implements AccountManager {
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
    public Account forAccount(long qq) {
        return CollectionUtility.getOrSupplie(loadedAccounts, qq,
                () -> getXiaomingBot().getFileLoader().loadOrSupplie(AccountImpl.class, accountFile(qq),
                        () -> {
                            final AccountImpl account = new AccountImpl();
                            account.setCode(qq);
                            return account;
                        }));
    }
}
