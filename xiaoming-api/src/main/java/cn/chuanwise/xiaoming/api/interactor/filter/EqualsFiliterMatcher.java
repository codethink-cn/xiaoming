package cn.chuanwise.xiaoming.api.interactor.filter;

import cn.chuanwise.xiaoming.api.contact.message.Message;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.Objects;

public class EqualsFiliterMatcher extends StringFilterMatcher {
    public EqualsFiliterMatcher(String string) {
        super(string);
    }

    @Override
    public boolean apply(XiaomingUser user, Message message) {
        return Objects.equals(message.serialize(), string);
    }
}
