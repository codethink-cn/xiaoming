package cn.chuanwise.xiaoming.account;

import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.tag.TagHolder;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public interface AccountManager extends ModuleObject {
    File accountFile(long code);

    Account getAccount(long code);

    File getDirectory();

    Map<Long, Account> getLoadedAccounts();

    default String getAliasOrCode(long code) {
        final Account account = getAccount(code);
        if (Objects.isNull(account)) {
            return String.valueOf(code);
        } else {
            return account.getAliasOrCode();
        }
    }

    default String getAliasAndCode(long code) {
        final Account account = getAccount(code);
        if (Objects.isNull(account)) {
            return String.valueOf(code);
        } else {
            return account.getAliasAndCode();
        }
    }

    default Set<String> getTags(long code) {
        final Account account = getAccount(code);
        if (Objects.isNull(account)) {
            return CollectionUtility.asSet(TagHolder.RECORDED, String.valueOf(code));
        } else {
            return account.getTags();
        }
    }

    default boolean hasTag(long code, String tag) {
        return getTags(code).contains(tag);
    }
}
