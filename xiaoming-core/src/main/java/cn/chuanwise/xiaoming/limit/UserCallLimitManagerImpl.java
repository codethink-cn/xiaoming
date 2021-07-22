package cn.chuanwise.xiaoming.limit;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class UserCallLimitManagerImpl implements UserCallLimitManager {
    CallLimiter groupCallLimiter = new CallLimiterImpl();
    CallLimiter privateCallLimiter = new CallLimiterImpl();

    @Setter
    transient XiaomingBot xiaomingBot;

    public UserCallLimitManagerImpl(XiaomingBot xiaomingBot) {
        this.xiaomingBot = xiaomingBot;
    }

    @Override
    public CallLimiter getGroupCallLimiter() {
        return groupCallLimiter;
    }

    public void setGroupCallLimiter(CallLimiter groupCallLimiter) {
        this.groupCallLimiter = groupCallLimiter;
    }

    @Override
    public CallLimiter getPrivateCallLimiter() {
        return privateCallLimiter;
    }

    public void setPrivateCallLimiter(CallLimiter privateCallLimiter) {
        this.privateCallLimiter = privateCallLimiter;
    }

    @Override
    public XiaomingBot getXiaomingBot() {
        return xiaomingBot;
    }
}
