package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaomingUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartMatchFilterMatcher extends RegexFilterMatcher {
    public StartMatchFilterMatcher(Pattern pattern) {
        super(pattern);
    }

    @Override
    public boolean apply(XiaomingUser user, Message message) {
        final Matcher matcher = pattern.matcher(message.serialize());
        return matcher.find() && matcher.start() == 0;
    }
}
