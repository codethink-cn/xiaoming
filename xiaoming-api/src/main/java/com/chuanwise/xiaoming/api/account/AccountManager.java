package com.chuanwise.xiaoming.api.account;

import com.chuanwise.xiaoming.api.object.ModuleObject;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public interface AccountManager extends ModuleObject {
    File accountFile(long qq);

    Account getAccount(long qq);

    Account getOrPutAccount(long qq);

    File getDirectory();

    Map<Long, Account> getLoadedAccounts();

    default String getAliasOrCode(long qq) {
        final Account account = getAccount(qq);
        if (Objects.isNull(account)) {
            return String.valueOf(qq);
        } else {
            return account.getAlias();
        }
    }

    default String getAliasAndCode(long qq) {
        final Account account = getAccount(qq);
        if (Objects.isNull(account)) {
            return String.valueOf(qq);
        } else {
            return account.getCompleteName();
        }
    }

    default boolean hasTag(long qq, String tag) {
        if (tag == String.valueOf(qq)) {
            return true;
        }

        final Account account = getAccount(qq);
        if (Objects.nonNull(account)) {
            if (Objects.equals("recorded", tag)) {
                return true;
            } else {
                return account.hasTag(tag);
            }
        } else {
            return false;
        }
    }
}
