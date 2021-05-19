package com.chuanwise.xiaoming.core.limit;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.limit.UserCallLimitManager;
import com.chuanwise.xiaoming.api.limit.UserCallLimiter;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class UserCallLimitManagerImpl implements UserCallLimitManager {
    UserCallLimiterImpl groupCallLimiter = new UserCallLimiterImpl();
    UserCallLimiterImpl privateCallLimiter = new UserCallLimiterImpl();

    @Setter
    transient XiaomingBot xiaomingBot;

    public UserCallLimitManagerImpl(XiaomingBot xiaomingBot) {
        this.xiaomingBot = xiaomingBot;
    }

    @Override
    public UserCallLimiter getGroupCallLimiter() {
        return groupCallLimiter;
    }

    public void setGroupCallLimiter(UserCallLimiterImpl groupCallLimiter) {
        this.groupCallLimiter = groupCallLimiter;
    }

    @Override
    public UserCallLimiter getPrivateCallLimiter() {
        return privateCallLimiter;
    }

    public void setPrivateCallLimiter(UserCallLimiterImpl privateCallLimiter) {
        this.privateCallLimiter = privateCallLimiter;
    }

    @Override
    public XiaomingBot getXiaomingBot() {
        return xiaomingBot;
    }
}
