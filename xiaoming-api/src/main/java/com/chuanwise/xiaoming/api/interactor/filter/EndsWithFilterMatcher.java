package com.chuanwise.xiaoming.api.interactor.filter;

import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

public class EndsWithFilterMatcher extends StringFilterMatcher {
    public EndsWithFilterMatcher(String string) {
        super(string);
    }

    @Override
    public boolean apply(XiaomingUser user, Message message) {
        return message.serialize().endsWith(string);
    }
}
