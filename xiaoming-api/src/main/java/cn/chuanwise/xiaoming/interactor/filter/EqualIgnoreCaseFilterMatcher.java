package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaomingUser;

public class EqualIgnoreCaseFilterMatcher extends StringFilterMatcher {
    public EqualIgnoreCaseFilterMatcher(String string) {
        super(string);
    }

    @Override
    public <M extends Message> boolean apply(XiaomingUser<?, M, ?> user, M message) {
        return string.equalsIgnoreCase(message.serialize());
    }
}
