package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.xiaoming.annotation.Customizable;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.limit.CallLimitConfiguration;
import cn.chuanwise.xiaoming.limit.UserCallLimitManager;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.utility.CommandWords;
import cn.chuanwise.xiaoming.interactor.InteractorImpl;

import java.util.concurrent.TimeUnit;

/**
 * 和小明调用限制相关的指令处理器
 * @author Chuanwise
 */
public class CallLimitInteractor extends InteractorImpl {
    final UserCallLimitManager callLimitManager;
    final CallLimitConfiguration groupCallConfig, privateCallConfig;

    static final String TOP = "(上限|top)";
    static final String PERIOD = "(周期|period)";
    static final String COOLDOWN = "(冷却|冷却时间|cooldown)";

    public CallLimitInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        callLimitManager = getXiaomingBot().getUserCallLimitManager();
        groupCallConfig = getXiaomingBot().getConfiguration().getGroupCallConfig();
        privateCallConfig = getXiaomingBot().getConfiguration().getPrivateCallConfig();
    }

    /** 群聊调用相关 */
    @Customizable("clearGroupCallRecord")
    @Filter(CommandWords.CLEAR + CommandWords.GROUP + CommandWords.CALL + CommandWords.RECORD)
    @Permission("limit.group.clear")
    public void onClearGroupCallRecord(XiaomingUser user) {
        callLimitManager.getGroupCallLimiter().getCallRecords().clear();
        user.sendMessage("{lang.groupCallRecordCleared}");
    }

    @Customizable("lookGroupCallLimit")
    @Filter(CommandWords.GROUP + CommandWords.CALL + CommandWords.LIMIT)
    @Permission("limit.group.look")
    public void onLookGroupCallLimit(XiaomingUser user) {
        user.sendMessage("{lang.groupCallLimit}");
        if (user.hasPermission("limit.group.bypass")) {
            user.sendMessage("{lang.butItIsNotWorkForYou}");
        }
    }

    @Customizable("setGroupCallPeriod")
    @Filter(CommandWords.SET + CommandWords.GROUP + CommandWords.CALL + PERIOD + " {period}")
    @Permission("limit.group.period")
    public void onSetGroupCallPeriod(XiaomingUser user,
                                     @FilterParameter("period") long period) {
        final CallLimitConfiguration configuration = callLimitManager.getGroupCallLimiter().getConfiguration();
        configuration.setPeriod(period);
        getXiaomingBot().getFileSaver().readyToSave(getXiaomingBot().getConfiguration());
        user.sendMessage("{lang.setGroupCallLimitPeriod}");
    }

    @Customizable("setGroupCallTop")
    @Filter(CommandWords.SET + CommandWords.GROUP + CommandWords.CALL + TOP + " {top}")
    @Permission("limit.group.top")
    public void onSetGroupCallTop(XiaomingUser user,
                                  @FilterParameter("top") int top) {
        if (top <= 0) {
            user.sendError("{lang.illegalGroupCallLimitTop}", top);
            return;
        }
        if (top < 5) {
            user.sendWarning("{lang.groupCallLimitTopTooSmall}", top);
            return;
        }
        final CallLimitConfiguration configuration = callLimitManager.getGroupCallLimiter().getConfiguration();
        configuration.setTop(top);
        getXiaomingBot().getFileSaver().readyToSave(getXiaomingBot().getConfiguration());
        callLimitManager.getGroupCallLimiter().getCallRecords().clear();
        user.sendMessage("{lang.setGroupCallLimitTop}");
    }

    @Customizable("setGroupCallCooldown")
    @Filter(CommandWords.SET + CommandWords.GROUP + CommandWords.CALL + COOLDOWN + " {time}")
    @Permission("limit.group.cooldown")
    public void onSetGroupCallCoolDown(XiaomingUser user,
                                       @FilterParameter("time") long cooldown) {
        if (cooldown > TimeUnit.SECONDS.toMillis(30)) {
            user.sendError("{lang.groupCallLimitCooldownTooLong}", cooldown);
            return;
        }
        final CallLimitConfiguration configuration = callLimitManager.getGroupCallLimiter().getConfiguration();
        configuration.setCoolDown(cooldown);
        getXiaomingBot().getFileSaver().readyToSave(getXiaomingBot().getConfiguration());
        user.sendMessage("{lang.setGroupCallLimitCooldown}");
    }


    /** 私聊调用相关 */
    @Customizable("clearPrivateCallRecord")
    @Filter(CommandWords.CLEAR + CommandWords.PRIVATE + CommandWords.CALL + CommandWords.RECORD)
    @Permission("limit.private.clear")
    public void onClearPrivateCallRecord(XiaomingUser user) {
        callLimitManager.getPrivateCallLimiter().getCallRecords().clear();
        user.sendMessage("{lang.privateCallRecordCleared}");
    }

    @Customizable("lookPrivateCallLimit")
    @Filter(CommandWords.PRIVATE + CommandWords.CALL + CommandWords.LIMIT)
    @Permission("limit.private.look")
    public void onLookPrivateCallLimit(XiaomingUser user) {
        user.sendMessage("{lang.privateCallLimit}");
        if (user.hasPermission("limit.private.bypass")) {
            user.sendMessage("{lang.butItIsNotWorkForYou}");
        }
    }

    @Customizable("setPrivateCallPeriod")
    @Filter(CommandWords.SET + CommandWords.PRIVATE + CommandWords.CALL + PERIOD + " {period}")
    @Permission("limit.private.period")
    public void onSetPrivateCallPeriod(XiaomingUser user,
                                       @FilterParameter("period") long period) {
        final CallLimitConfiguration configuration = callLimitManager.getPrivateCallLimiter().getConfiguration();
        configuration.setPeriod(period);
        getXiaomingBot().getFileSaver().readyToSave(getXiaomingBot().getConfiguration());
        user.sendMessage("{lang.setPrivateCallLimitPeriod}");
    }

    @Customizable("setPrivateCallTop")
    @Filter(CommandWords.SET + CommandWords.PRIVATE + CommandWords.CALL + TOP + " {top}")
    @Permission("limit.private.top")
    public void onSetPrivateCallTop(XiaomingUser user,
                                    @FilterParameter("top") int top) {
        if (top <= 0) {
            user.sendError("{lang.illegalPrivateCallLimitTop}", top);
            return;
        }
        if (top < 5) {
            user.sendWarning("{lang.privateCallLimitTopTooSmall}", top);
            return;
        }
        final CallLimitConfiguration configuration = callLimitManager.getPrivateCallLimiter().getConfiguration();
        configuration.setTop(top);
        getXiaomingBot().getFileSaver().readyToSave(getXiaomingBot().getConfiguration());
        callLimitManager.getPrivateCallLimiter().getCallRecords().clear();
        user.sendMessage("{lang.setPrivateCallLimitTop}");
    }

    @Customizable("setPrivateCallCoolDown")
    @Filter(CommandWords.SET + CommandWords.PRIVATE + CommandWords.CALL + COOLDOWN + " {time}")
    @Permission("limit.private.cooldown")
    public void onSetPrivateCallCoolDown(XiaomingUser user,
                                         @FilterParameter("time") long cooldown) {
        if (cooldown > TimeUnit.SECONDS.toMillis(30)) {
            user.sendError("{lang.privateCallLimitCooldownTooLong}", cooldown);
            return;
        }
        final CallLimitConfiguration configuration = callLimitManager.getPrivateCallLimiter().getConfiguration();
        configuration.setCoolDown(cooldown);
        getXiaomingBot().getFileSaver().readyToSave(getXiaomingBot().getConfiguration());
        user.sendMessage("{lang.setPrivateCallLimitCooldown}");
    }
}