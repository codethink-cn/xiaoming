package cn.chuanwise.xiaoming.api.interactor.filter;

import cn.chuanwise.xiaoming.api.contact.message.Message;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartsRegexFilterMatcher extends RegexFilterMatcher {
    public StartsRegexFilterMatcher(Pattern pattern) {
        super(pattern);
    }

    @Override
    public boolean apply(XiaomingUser user, Message message) {
        final Matcher matcher = pattern.matcher(message.serialize());
        return matcher.find() && matcher.start() == 0;
    }
}
