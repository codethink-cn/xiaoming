package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaomingUser;

public class EndsWithFilterMatcher extends StringFilterMatcher {
    public EndsWithFilterMatcher(String string) {
        super(string);
    }

    @Override
    public boolean apply(XiaomingUser user, Message message) {
        return message.serialize().endsWith(string);
    }
}
