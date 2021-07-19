package cn.chuanwise.xiaoming.api.interactor.filter;

import cn.chuanwise.xiaoming.api.contact.message.Message;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;

public class EndsWithFilterMatcher extends StringFilterMatcher {
    public EndsWithFilterMatcher(String string) {
        super(string);
    }

    @Override
    public boolean apply(XiaomingUser user, Message message) {
        return message.serialize().endsWith(string);
    }
}
