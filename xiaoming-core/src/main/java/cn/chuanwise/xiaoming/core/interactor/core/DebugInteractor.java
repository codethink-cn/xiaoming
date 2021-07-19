package cn.chuanwise.xiaoming.core.interactor.core;

import cn.chuanwise.xiaoming.api.annotation.Filter;
import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.interactor.Interactor;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;
import cn.chuanwise.xiaoming.core.interactor.InteractorImpl;

import java.util.Set;

public class DebugInteractor extends InteractorImpl {
    public DebugInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
    }

    @Filter("debug1")
    public void onDebug1(XiaomingUser user) {
        final Set<Interactor> coreInteractors = getXiaomingBot().getInteractorManager().getCoreInteractors();
    }
}
