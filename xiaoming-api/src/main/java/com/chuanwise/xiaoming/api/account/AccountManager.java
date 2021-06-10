package com.chuanwise.xiaoming.api.account;

import com.chuanwise.xiaoming.api.object.ModuleObject;

import java.io.File;
import java.util.Map;

public interface AccountManager extends ModuleObject {
    File accountFile(long qq);

    Account getAccount(long qq);

    Account getOrPutAccount(long qq);

    File getDirectory();

    Map<Long, Account> getLoadedAccounts();
}
