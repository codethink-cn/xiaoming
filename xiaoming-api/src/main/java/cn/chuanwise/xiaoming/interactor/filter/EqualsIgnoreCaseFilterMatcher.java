package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaomingUser;

public class EqualsIgnoreCaseFilterMatcher extends StringFilterMatcher {
    public EqualsIgnoreCaseFilterMatcher(String string) {
        super(string);
    }

    @Override
    public boolean apply(XiaomingUser user, Message message) {
        return string.equalsIgnoreCase(message.serialize());
    }
}
