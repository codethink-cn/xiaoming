package com.chuanwise.xiaoming.api.interactor.filter;

import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chuanwise
 */
public class EndsRegexFilterMatcher extends RegexFilterMatcher {
    public EndsRegexFilterMatcher(Pattern pattern) {
        super(pattern);
    }

    @Override
    public boolean apply(XiaomingUser user) {
        final String message = user.getMessage();
        final Matcher matcher = pattern.matcher(message);
        return matcher.find() && matcher.end() == message.length();
    }
}
