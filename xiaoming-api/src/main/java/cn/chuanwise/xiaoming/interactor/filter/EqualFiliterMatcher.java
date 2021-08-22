package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaomingUser;

import java.util.Objects;

public class EqualFiliterMatcher extends StringFilterMatcher {
    public EqualFiliterMatcher(String string) {
        super(string);
    }

    @Override
    public <M extends Message> boolean apply(XiaomingUser<?, M, ?> user, M message) {
        return Objects.equals(message.serialize(), string);
    }
}
