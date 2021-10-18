package cn.chuanwise.xiaoming.account;

import cn.chuanwise.toolkit.preservable.AbstractPreservable;
import cn.chuanwise.util.MapUtil;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class AccountManagerImpl
        extends AbstractPreservable
        implements AccountManager {
    final Map<Long, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public Map<Long, Account> getAccounts() {
        return Collections.unmodifiableMap(accounts);
    }

    @Override
    public Account createAccount(long code) {
        return MapUtil.getOrPutSupply(accounts, code,
                () -> {
                    final AccountImpl account = new AccountImpl();
                    account.setCode(code);

                    final List<XiaomingContact> contacts = xiaomingBot.getContactManager().getPrivateContactPossibly(code);
                    if (!contacts.isEmpty()) {
                        account.setAlias(contacts.get(0).getName());
                    }

                    return account;
                });
    }

    @Setter
    transient XiaomingBot xiaomingBot;
    transient Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Transient
    @Override
    public XiaomingBot getXiaomingBot() {
        return xiaomingBot;
    }
}
