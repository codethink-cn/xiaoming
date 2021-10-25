package cn.chuanwise.xiaoming.account;

import cn.chuanwise.api.TagMarkable;
import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.object.ModuleObject;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public interface AccountManager extends Preservable, ModuleObject {
    Account createAccount(long code);

    Map<Long, Account> getAccounts();

    default Optional<Account> getAccount(long code) {
        return Optional.ofNullable(getAccounts().get(code));
    }

    default Optional<String> getAlias(long code) {
        return getAccount(code).map(Account::getAlias);
    }

    default String getAliasOrCode(long code) {
        return getAccount(code)
                .map(Account::getAliasOrCode)
                .orElseGet(() -> {
                    final List<XiaomingContact> contacts = getXiaomingBot().getContactManager().getPrivateContactPossibly(code);
                    return contacts.isEmpty() ? String.valueOf(code) : contacts.get(0).getName();
                });
    }

    default String getAliasAndCode(long code) {
        return getAccount(code)
                .map(Account::getAliasAndCode)
                .orElseGet(() -> {
                    final List<XiaomingContact> contacts = getXiaomingBot().getContactManager().getPrivateContactPossibly(code);
                    return contacts.isEmpty() ? String.valueOf(code) : (contacts.get(0).getName() + "（" + code + "）");
                });
    }

    default Set<String> getTags(long code) {
        return getAccount(code)
                .map(TagMarkable::getTags)
                .orElseGet(() -> Account.originalTagsOf(code));
    }

    default List<Account> searchAccountsByTag(String tag) {
        return getAccounts().values().stream()
                .filter(account -> account.hasTag(tag))
                .collect(Collectors.toUnmodifiableList());
    }
}
