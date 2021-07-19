package cn.chuanwise.xiaoming.api.interactor.filter;

import cn.chuanwise.xiaoming.api.contact.message.Message;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;

public class EqualsIgnoreCaseFilterMatcher extends StringFilterMatcher {
    public EqualsIgnoreCaseFilterMatcher(String string) {
        super(string);
    }

    @Override
    public boolean apply(XiaomingUser user, Message message) {
        return string.equalsIgnoreCase(message.serialize());
    }
}
