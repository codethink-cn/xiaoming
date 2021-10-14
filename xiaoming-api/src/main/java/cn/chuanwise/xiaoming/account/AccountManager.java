package cn.chuanwise.xiaoming.account;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.toolkit.tag.TagMarkable;

import java.io.File;
import java.util.List;
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
//            final List<XiaomingContact> contacts = getXiaomingBot().getContactManager().getPrivateContactPossibly(code);
//            if (contacts.isEmpty()) {
//                return String.valueOf(code);
//            } else {
//                return contacts.get(0).getName();
//            }

            return String.valueOf(code);
        } else {
            return account.getAliasOrCode();
        }
    }

    default String getAliasAndCode(long code) {
        final Account account = getAccount(code);
        if (Objects.isNull(account)) {
//            final List<XiaomingContact> contacts = getXiaomingBot().getContactManager().getPrivateContactPossibly(code);
//            if (contacts.isEmpty()) {
//                return String.valueOf(code);
//            } else {
//                return contacts.get(0).getName() + "（" + code + "）";
//            }

            return String.valueOf(code);
        } else {
            return account.getAliasAndCode();
        }
    }

    default Set<String> getTags(long code) {
        final Account account = getAccount(code);
        if (Objects.isNull(account)) {
            return CollectionUtil.asSet(TagMarkable.RECORDED, String.valueOf(code));
        } else {
            return account.getTags();
        }
    }

    default boolean hasTag(long code, String tag) {
        return getTags(code).contains(tag);
    }
}
