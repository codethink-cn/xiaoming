package cn.chuanwise.xiaoming.core.account;

import cn.chuanwise.toolkit.preservable.file.FileLoader;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.xiaoming.api.account.Account;
import cn.chuanwise.xiaoming.api.account.AccountManager;
import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.core.object.ModuleObjectImpl;
import lombok.Getter;

import java.io.File;
import java.util.Map;
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
