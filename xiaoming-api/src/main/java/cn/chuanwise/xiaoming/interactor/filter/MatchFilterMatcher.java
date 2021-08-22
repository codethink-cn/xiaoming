package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaomingUser;

import java.util.regex.Pattern;

public class MatchFilterMatcher extends RegexFilterMatcher {
    public MatchFilterMatcher(Pattern pattern) {
        super(pattern);
    }

    @Override
    public <M extends Message> boolean apply(XiaomingUser<?, M, ?> user, M message) {
        return pattern.matcher(message.serialize()).matches();
    }
}
