package com.chuanwise.xiaoming.api.interactor.filter;

import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.Objects;

public class EqualsFiliterMatcher extends StringFilterMatcher {
    public EqualsFiliterMatcher(String string) {
        super(string);
    }

    @Override
    public boolean apply(XiaomingUser user) {
        return Objects.equals(user, string);
    }
}
