package cn.chuanwise.xiaoming.api.interactor.filter;

import cn.chuanwise.xiaoming.api.contact.message.Message;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.regex.Pattern;

public class MatchFilterMatcher extends RegexFilterMatcher {
    public MatchFilterMatcher(Pattern pattern) {
        super(pattern);
    }

    @Override
    public boolean apply(XiaomingUser user, Message message) {
        return pattern.matcher(message.serialize()).matches();
    }
}
