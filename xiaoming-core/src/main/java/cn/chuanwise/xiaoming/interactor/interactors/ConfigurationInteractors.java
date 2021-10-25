package cn.chuanwise.xiaoming.interactor.interactors;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Required;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;

public class ConfigurationInteractors extends SimpleInteractors {
    Configuration configuration;

    @Override
    public void onRegister() {
        configuration = xiaomingBot.getConfiguration();
    }

    /** 调试模式开关 */
    @Filter(CommandWords.ENABLE + CommandWords.DEBUG)
    @Required("debug.enable")
    public void onEnableDebug(XiaomingUser user) {
        if (configuration.isDebug()) {
            user.sendError("{lang.debugModeAlreadyEnabled}");
        } else {
            configuration.setDebug(true);
            getXiaomingBot().getFileSaver().readyToSave(configuration);
            user.sendMessage("{lang.debugModeEnabledSuccessfully}");
        }
    }

    @Filter(CommandWords.DISABLE + CommandWords.DEBUG)
    @Required("debug.enable")
    public void onDisableDebug(XiaomingUser user) {
        if (configuration.isDebug()) {
            configuration.setDebug(false);
            getXiaomingBot().getFileSaver().readyToSave(configuration);
            user.sendMessage("{lang.debugModeDisabledSuccessfully}");
        } else {
            user.sendMessage("{lang.debugModeAlreadyDisabled}");
        }
    }

    /** 保存文件周期等 */
    @Filter(CommandWords.SET + CommandWords.SAVE + CommandWords.PERIOD + " {时长}")
    @Required("config.save.period.set")
    public void onSetSavePeriod(XiaomingUser user, @FilterParameter("时长") long saveperiod) {
        configuration.setSavePeriod(saveperiod);
        getXiaomingBot().getFileSaver().readyToSave(configuration);

        if (configuration.isSaveFileDirectly()) {
            user.sendWarning("{lang.savePeriodSetButSaveFileDirectlyEnabled}");
        } else {
            user.sendMessage("{lang.savePeriodSet}");
        }
    }

    @Filter(CommandWords.SET + CommandWords.AUTO + CommandWords.OPTIMIZE + CommandWords.PERIOD + " {时长}")
    @Required("config.optimize.period.set")
    public void onSetOptimizePeriod(XiaomingUser user, @FilterParameter("时长") long savePeriod) {
        configuration.setOptimizePeriod(savePeriod);
        getXiaomingBot().getFileSaver().readyToSave(configuration);
        user.sendMessage("{lang.optimizePeriodSet}");
    }

    /** 查看设置 */
    @Filter(CommandWords.LOOK + CommandWords.XIAOMING + CommandWords.CONFIGURE)
    @Required("config.list")
    public void onListConfiguration(XiaomingUser user) {
        user.sendPrivateMessage("{lang.listConfiguration}");
    }
}
