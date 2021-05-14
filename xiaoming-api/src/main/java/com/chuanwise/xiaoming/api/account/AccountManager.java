package com.chuanwise.xiaoming.api.account;

import com.chuanwise.xiaoming.api.object.HostObject;

import java.io.File;

public interface AccountManager extends HostObject {
    File accountFile(long qq);

    Account getAccount(long qq);

    Account getOrPutAccount(long qq);

    File getDirectory();

    java.util.Map<Long, Account> getLoadedAccounts();
}
