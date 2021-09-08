package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.utility.StringUtility;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.utility.CommandWords;
import cn.chuanwise.xiaoming.utility.InteractorUtility;

import java.util.Set;

public class ConfigurationInteractors extends SimpleInteractors {
    Configuration configuration;

    @Override
    public void onRegister() {
        configuration = xiaomingBot.getConfiguration();
    }

    /** 调试模式开关 */
    @Filter(CommandWords.ENABLE + CommandWords.DEBUG)
    @Permission("debug.enable")
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
    @Permission("debug.enable")
    public void onDisableDebug(XiaomingUser user) {
        if (configuration.isDebug()) {
            configuration.setDebug(false);
            getXiaomingBot().getFileSaver().readyToSave(configuration);
            user.sendMessage("{lang.debugModeDisabledSuccessfully}");
        } else {
            user.sendMessage("{lang.debugModeAlreadyDisabled}");
        }
    }

    /** 明确调用 */
    @Filter(CommandWords.ENABLE + CommandWords.CLEAN + CommandWords.CALL)
    @Permission("config.clearcall.enable")
    public void onEnableClearCall(XiaomingUser user) {
        if (configuration.isEnableClearCall()) {
            user.sendMessage("{lang.clearCallAlreadyEnabled}");
        } else {
            final Set<String> callPrefixes = configuration.getClearCallPrefixes();
            if (callPrefixes.isEmpty()) {
                user.sendMessage("{lang.queryClearCallPrefixes}");
                InteractorUtility.fillStringCollection(user,
                        callPrefixes,
                        user.format("{lang.clearCallPrefixes}"));
            }
            configuration.setEnableClearCall(true);
            user.sendMessage("{lang.clearCallEnabled}");
            getXiaomingBot().getFileSaver().readyToSave(getXiaomingBot().getConfiguration());
        }
    }

    @Filter(CommandWords.CLEAN + CommandWords.CALL)
    @Permission("config.clearcall.look")
    public void onListClearCallPrefix(XiaomingUser user) {
        if (configuration.isEnableClearCall()) {
            user.sendMessage("{lang.clearCallDetail}");
        } else {
            user.sendMessage("{lang.clearCallHadNotEnabled}");
        }
    }

    @Filter(CommandWords.SET + CommandWords.CLEAN + CommandWords.CALL + CommandWords.MESSAGE + CommandWords.HEAD)
    @Filter(CommandWords.EDIT + CommandWords.CLEAN + CommandWords.CALL + CommandWords.MESSAGE + CommandWords.HEAD)
    @Permission("config.clearcall.head.set")
    public void onSetClearCallPrefix(XiaomingUser user) {
        final Set<String> callPrefixes = configuration.getClearCallPrefixes();
        callPrefixes.clear();
        user.sendMessage("{lang.queryClearCallPrefixes}");
        InteractorUtility.fillStringCollection(user,
                callPrefixes,
                user.format("{lang.clearCallPrefixes}"));
        configuration.setEnableClearCall(true);

        getXiaomingBot().getFileSaver().readyToSave(configuration);
        if (configuration.isEnableClearCall()) {
            user.sendMessage("{lang.clearCallPrefixesSetAndEnabled}");
        } else {
            user.sendMessage("{lang.clearCallPrefixesSetButNotEnabled}");
        }
    }

    @Filter(CommandWords.DISABLE + CommandWords.CLEAN + CommandWords.CALL)
    @Permission("config.clearcall.disable")
    public void onDisableClearCall(XiaomingUser user) {
        if (configuration.isEnableClearCall()) {
            configuration.setEnableClearCall(false);
            getXiaomingBot().getFileSaver().readyToSave(configuration);
            user.sendMessage("{lang.clearCallDisabled}");
        } else {
            user.sendMessage("{lang.clearCallHadNotEnabled}");
        }
    }

    /** 使用小明验证 */
    @Filter(CommandWords.ENABLE + CommandWords.USE + CommandWords.LICENSE)
    @Permission("config.license.enable")
    public void onEnableLicense(XiaomingUser user) {
        if (configuration.isEnableLicense()) {
            user.sendMessage("{lang.licenseAlreadyEnabled}");
        } else {
            final String agreement = getXiaomingBot().getLicenseManager().getLicense();
            if (StringUtility.isEmpty(agreement)) {
                user.sendMessage("{lang.pleaseEnterLicense}");
                getXiaomingBot().getLicenseManager().setLicense(user.nextMessageOrExit().serialize());
                user.sendMessage("{lang.licenseEnabled}");
            } else {
                user.sendMessage("{lang.licenseEnabledWithElderLicense}");
            }
            configuration.setEnableLicense(true);
            getXiaomingBot().getFileSaver().readyToSave(configuration);
        }
    }

    @Filter(CommandWords.USE + CommandWords.LICENSE)
    @Filter(CommandWords.USE + CommandWords.XIAOMING + CommandWords.LICENSE)
    @Permission("config.license.look")
    public void onLookLicense(XiaomingUser user) {
        if (configuration.isEnableLicense()) {
            user.sendMessage("{bot.licenseManager.license}");
        } else {
            user.sendMessage("{lang.licenseHadNotEnabled}");
        }
    }

    @Filter(CommandWords.DISABLE + CommandWords.USE + CommandWords.LICENSE)
    @Permission("config.license.disable")
    public void onDisableLicense(XiaomingUser user) {
        if (configuration.isEnableLicense()) {
            configuration.setEnableLicense(false);
            getXiaomingBot().getFileSaver().readyToSave(configuration);
            user.sendMessage("{lang.licenseDisabled}");
        } else {
            user.sendMessage("{lang.licenseHadNotEnabled}");
        }
    }

    /** 保存文件周期等 */
    @Filter(CommandWords.SET + CommandWords.SAVE + CommandWords.PERIOD + " {时长}")
    @Permission("config.save.period.set")
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
    @Permission("config.optimize.period.set")
    public void onSetOptimizePeriod(XiaomingUser user, @FilterParameter("时长") long savePeriod) {
        configuration.setOptimizePeriod(savePeriod);
        getXiaomingBot().getFileSaver().readyToSave(configuration);
        user.sendMessage("{lang.optimizePeriodSet}");
    }

    /** 查看设置 */
    @Filter(CommandWords.LOOK + CommandWords.XIAOMING + CommandWords.CONFIGURE)
    @Permission("config.list")
    public void onListConfiguration(XiaomingUser user) {
        user.sendPrivateMessage("{lang.listConfiguration}");
    }
}
