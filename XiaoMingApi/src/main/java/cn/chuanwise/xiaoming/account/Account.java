package cn.chuanwise.xiaoming.account;

import cn.chuanwise.api.OriginalTagMarkable;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.StringUtil;
import cn.chuanwise.util.TagUtil;

import java.util.Objects;
import java.util.Set;

public interface Account extends OriginalTagMarkable {
    long getCode();

    boolean isAdministrator();

    void setAdministrator(boolean administrator);

    boolean isBanned();

    void setBanned(boolean banned);

    default String getCodeString() {
        return String.valueOf(getCode());
    }

    default String getAliasAndCode() {
        final String alias = getAlias();
        return Objects.nonNull(alias) ? (alias + "（" + getCodeString() + "）") : getCodeString();
    }

    default String getAliasOrCode() {
        return StringUtil.firstNonEmpty(getAlias(), getCodeString());
    }

    String getAlias();

    void setCode(long code);

    void setAlias(String alias);

    @Override
    default Set<String> getOriginalTags() {
        return originalTagsOf(getCode());
    }

    static Set<String> originalTagsOf(long code) {
        return CollectionUtil.asSet(String.valueOf(code), TagUtil.ALL);
    }
}
