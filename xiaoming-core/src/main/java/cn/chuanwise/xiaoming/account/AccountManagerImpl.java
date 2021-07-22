package cn.chuanwise.xiaoming.account;

import cn.chuanwise.toolkit.sized.SizedResidentConcurrentHashMap;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import lombok.Getter;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class AccountManagerImpl extends ModuleObjectImpl implements AccountManager {
    final File directory;

    final Map<Long, Account> loadedAccounts;

    public AccountManagerImpl(XiaomingBot xiaomingBot, File directory) {
        super(xiaomingBot);
        this.directory = directory;
        this.loadedAccounts = new SizedResidentConcurrentHashMap<>(xiaomingBot.getConfiguration().getMaxLoadedAccountQuantity());
    }

    @Override
    public File accountFile(long qq) {
        return new File(directory, qq + ".json");
    }

    @Override
    public Account forAccount(long qq) {
        return CollectionUtility.getOrPutSupplie(loadedAccounts, qq,
                () -> getXiaomingBot().getFileLoader().loadOrSupplie(AccountImpl.class, accountFile(qq),
                        () -> {
                            final AccountImpl account = new AccountImpl();
                            account.setCode(qq);
                            return account;
                        }));
    }
}
