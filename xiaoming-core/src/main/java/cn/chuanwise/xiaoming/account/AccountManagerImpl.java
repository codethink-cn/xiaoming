package cn.chuanwise.xiaoming.account;

import cn.chuanwise.toolkit.sized.SizedResidentConcurrentHashMap;
import cn.chuanwise.util.MapUtil;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import lombok.Getter;

import java.io.File;
import java.util.Map;

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
    public File accountFile(long code) {
        return new File(directory, code + ".json");
    }

    @Override
    public Account getAccount(long code) {
        return MapUtil.getOrPutSupply(loadedAccounts, code,
                () -> getXiaomingBot().getFileLoader().loadOrSupply(AccountImpl.class, accountFile(code),
                        () -> {
                            final AccountImpl account = new AccountImpl();
                            account.setCode(code);
                            return account;
                        }));
    }
}
