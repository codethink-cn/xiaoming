package com.chuanwise.xiaoming.api.interactor.filter;

import com.chuanwise.xiaoming.api.contact.message.Message;
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
    public boolean apply(XiaomingUser user, Message message) {
        final String serialize = message.serialize();
        final Matcher matcher = pattern.matcher(serialize);
        return matcher.find() && matcher.end() == serialize.length();
    }
}
