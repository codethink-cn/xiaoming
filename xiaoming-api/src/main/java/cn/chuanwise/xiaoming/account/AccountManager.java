package cn.chuanwise.xiaoming.account;

import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.xiaoming.object.ModuleObject;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public interface AccountManager extends ModuleObject {
    File accountFile(long qq);

    Account forAccount(long qq);

    File getDirectory();

    Map<Long, Account> getLoadedAccounts();

    default String getAliasOrCode(long qq) {
        final Account account = forAccount(qq);
        if (Objects.isNull(account)) {
            return String.valueOf(qq);
        } else {
            return account.getAlias();
        }
    }

    default String getAliasAndCode(long qq) {
        final Account account = forAccount(qq);
        if (Objects.isNull(account)) {
            return String.valueOf(qq);
        } else {
            return account.getCompleteName();
        }
    }

    default Set<String> getTags(long qq) {
        final Account account = forAccount(qq);
        if (Objects.isNull(account)) {
            return CollectionUtility.asSet("recorded", String.valueOf(qq));
        } else {
            return account.getTags();
        }
    }

    default boolean hasTag(long qq, String tag) {
        return getTags(qq).contains(tag);
    }
}
