package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaomingUser;

import java.util.regex.Pattern;

public class ContainMatchFilterMatcher extends RegexFilterMatcher {
    public ContainMatchFilterMatcher(Pattern pattern) {
        super(pattern);
    }

    @Override
    public boolean apply(XiaomingUser user, Message message) {
        return pattern.matcher(message.serialize()).find();
    }
}
