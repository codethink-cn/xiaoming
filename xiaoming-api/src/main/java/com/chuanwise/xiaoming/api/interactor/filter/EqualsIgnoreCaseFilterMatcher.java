package com.chuanwise.xiaoming.api.interactor.filter;

import com.chuanwise.xiaoming.api.user.XiaomingUser;

public class EqualsIgnoreCaseFilterMatcher extends StringFilterMatcher {
    public EqualsIgnoreCaseFilterMatcher(String string) {
        super(string);
    }

    @Override
    public boolean apply(XiaomingUser user) {
        return string.equalsIgnoreCase(user.getMessage());
    }
}
