package com.chuanwise.xiaoming.api.account;

import com.chuanwise.xiaoming.api.object.HostObject;

import java.io.File;
import java.util.Map;

public interface AccountManager extends HostObject {
    File accountFile(long qq);

    Account getAccount(long qq);

    Account getOrPutAccount(long qq);

    File getDirectory();

    Map<Long, Account> getLoadedAccounts();
}
