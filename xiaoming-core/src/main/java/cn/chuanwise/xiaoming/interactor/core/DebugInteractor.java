package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.message.GroupMessage;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.interactor.Interactor;
import cn.chuanwise.xiaoming.interactor.customizer.CustomizerImpl;
import cn.chuanwise.xiaoming.interactor.information.InteractorMethodInformation;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.interactor.InteractorImpl;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DebugInteractor extends InteractorImpl {
    public DebugInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
    }
}
