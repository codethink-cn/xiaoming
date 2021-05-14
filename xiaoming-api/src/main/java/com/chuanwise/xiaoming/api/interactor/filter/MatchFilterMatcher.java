package com.chuanwise.xiaoming.api.interactor.filter;

import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.regex.Pattern;

public class MatchFilterMatcher extends RegexFilterMatcher {
    public MatchFilterMatcher(Pattern pattern) {
        super(pattern);
    }

    @Override
    public boolean apply(XiaomingUser user) {
        return pattern.matcher(user.getMessage()).matches();
    }
}
