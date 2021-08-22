package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaomingUser;

public class EndEqualFilterMatcher extends StringFilterMatcher {
    public EndEqualFilterMatcher(String string) {
        super(string);
    }

    @Override
    public <M extends Message> boolean apply(XiaomingUser<?, M, ?> user, M message) {
        return message.serialize().endsWith(string);
    }
}
