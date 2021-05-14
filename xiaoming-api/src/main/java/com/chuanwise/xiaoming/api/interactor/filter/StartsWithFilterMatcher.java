package com.chuanwise.xiaoming.api.interactor.filter;

import com.chuanwise.xiaoming.api.user.XiaomingUser;

public class StartsWithFilterMatcher extends StringFilterMatcher {
    public StartsWithFilterMatcher(String string) {
        super(string);
    }

    @Override
    public boolean apply(XiaomingUser user) {
        return user.getMessage().startsWith(string);
    }
}