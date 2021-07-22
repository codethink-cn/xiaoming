package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.TimeUtility;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.utility.CommandWords;
import cn.chuanwise.xiaoming.utility.InteractorUtility;
import cn.chuanwise.xiaoming.interactor.InteractorImpl;

import java.util.Objects;
import java.util.Set;

public class ConfigurationInteractor extends InteractorImpl {
    final Configuration configuration;

    public ConfigurationInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        this.configuration = xiaomingBot.getConfiguration();
    }

    final static String CLEAR = "(明确|clear)";
    final static String DEBUG = "(维护|调试|debug)";
    final static String LICENSE = "(协议|license)";

    @Filter(CommandWords.ENABLE + DEBUG)
    @Permission("debug.enable")
    public void onEnableDebug(XiaomingUser user) {
        if (configuration.isDebug()) {
            user.sendMessage("{debugModeAlreadyEnabled}");
        } else {
            configuration.setDebug(true);
            getXiaomingBot().getFileSaver().readySave(configuration);
            user.sendMessage("{debugModeEnabledSuccessfully}");
        }
    }

    @Filter(CommandWords.DISABLE + DEBUG)
    @Permission("debug.enable")
    public void onDisableDebug(XiaomingUser user) {
        if (configuration.isDebug()) {
            configuration.setDebug(false);
            getXiaomingBot().getFileSaver().readySave(configuration);
            user.sendMessage("{debugModeDisabledSuccessfully}");
        } else {
            user.sendMessage("{debugModeHasNotBeenEnabled}");
        }
    }

    @Filter(CommandWords.ENABLE + CLEAR + CommandWords.CALL)
    @Permission("config.clearcall.enable")
    public void onEnableClearCall(XiaomingUser user) {
        if (configuration.isEnableClearCall()) {
            user.sendMessage("{clearCallAlreadyEnabled}");
        } else {
            final Set<String> callPrefixs = configuration.getClearCallPrefixes();
            InteractorUtility.fillStringCollection(user,
                    user.replaceLanguage("theMessageBeginWithWitchElementsShouldBeNoticed"),
                    user.replaceLanguage("clearCallPrefixSet"), callPrefixs, false);
            configuration.setEnableClearCall(true);

            user.setProperty("set", callPrefixs);
            user.sendMessage("{iWillOnlyMentionMessageStartsWithTheElementInThisSet}");
            getXiaomingBot().getFileSaver().readySave(getXiaomingBot().getConfiguration());
        }
    }

    @Filter(CLEAR + CommandWords.CALL)
    @Permission("config.clearcall.look")
    public void onListClearCallPrefix(XiaomingUser user) {
        if (configuration.isEnableClearCall()) {
            final Set<String> clearCallPrefixes = configuration.getClearCallPrefixes();
            if (clearCallPrefixes.isEmpty()) {
                user.sendMessage("当前具备 " + configuration.getClearCallGroupTag() + " 标记的群内启动了明确调用，但还没有设置任何消息开头的格式");
            } else {
                user.sendMessage("当前具备 " + configuration.getClearCallGroupTag() + " 标记的群内启动了明确调用，消息开头必须为 " +
                        CollectionUtility.toString(clearCallPrefixes, "、") + " 当中的一个");
            }
        } else {
            user.sendMessage("{clearCallHasNotBeenEnabled}");
        }
    }

    @Filter(CommandWords.SET + CLEAR + CommandWords.CALL + CommandWords.MESSAGE + CommandWords.HEAD)
    @Filter(CommandWords.EDIT + CLEAR + CommandWords.CALL + CommandWords.MESSAGE + CommandWords.HEAD)
    @Permission("config.clearcall.head.set")
    public void onSetClearCallPrefix(XiaomingUser user) {
        final Set<String> clearCallPrefixes = configuration.getClearCallPrefixes();
        clearCallPrefixes.clear();
        InteractorUtility.fillStringCollection(user, "以什么开头的消息需要被小明注意到呢", "明确调用消息头", clearCallPrefixes, false);

        getXiaomingBot().getFileSaver().readySave(configuration);
        user.sendMessage("成功修改明确调用消息头为：" + CollectionUtility.toString(clearCallPrefixes, "、"));
    }

    @Filter(CommandWords.DISABLE + CLEAR + CommandWords.CALL)
    @Permission("config.clearcall.disable")
    public void onDisableClearCall(XiaomingUser user) {
        if (configuration.isEnableClearCall()) {
            configuration.setEnableClearCall(false);
            getXiaomingBot().getFileSaver().readySave(configuration);
            user.sendMessage("{clearCallDisabledSuccessfully}");
        } else {
            user.sendMessage("{clearCallHasNotBeenEnabled}");
        }
    }

    @Filter(CommandWords.ENABLE + CommandWords.USE + LICENSE)
    @Permission("config.license.enable")
    public void onEnableCompulsoryAgreement(XiaomingUser user) {
        final Configuration config = getXiaomingBot().getConfiguration();
        if (config.isEnableLicense()) {
            user.sendMessage("{compulsoryAgreementAlreadyEnabled}");
        } else {
            String license = getXiaomingBot().getLanguage().getStringOrDefault("license", null);
            if (Objects.isNull(license)) {
                user.sendMessage("{inputLicense}");
                license = user.nextInput().serialize();
                getXiaomingBot().getLanguage().put("license", license);
            }
            config.setEnableLicense(true);
            user.sendMessage("enableCompulsoryAgreementSuccessfully");
            getXiaomingBot().getFileSaver().readySave(config);
        }
    }

    @Filter(CommandWords.USE + LICENSE)
    @Permission("config.license.look")
    public void onLookCompulsoryAgreement(XiaomingUser user) {
        final Configuration config = getXiaomingBot().getConfiguration();
        if (config.isEnableLicense()) {
            user.sendMessage(getXiaomingBot().getLanguage().getString("license"));
        } else {
            user.sendMessage("{compulsoryAgreementHasNotBeenEnabled}");
        }
    }

    @Filter(CommandWords.DISABLE + CommandWords.USE + LICENSE)
    @Permission("config.license.disable")
    public void onDisableCompulsoryAgreement(XiaomingUser user) {
        final Configuration config = getXiaomingBot().getConfiguration();
        if (config.isEnableLicense()) {
            config.setEnableLicense(false);
            getXiaomingBot().getFileSaver().readySave(config);
            user.sendMessage("{disableCompulsoryAgreementSuccessfully}");
        } else {
            user.sendMessage("{compulsoryAgreementHasNotBeenEnabled}");
        }
    }

    @Filter(CommandWords.SAVE + CommandWords.PERIOD)
    @Permission("config.save.period.look")
    public void onLookSavePeriod(XiaomingUser user) {
        user.setProperty("period", TimeUtility.toTimeLength(configuration.getSavePeriod()));
        user.sendMessage("{autoSavePeriodIs}");
    }

    @Filter(CommandWords.SET + CommandWords.SAVE + CommandWords.PERIOD + " {period}")
    @Permission("config.save.period.set")
    public void onSetSavePeriod(XiaomingUser user, @FilterParameter("period") long savePeriod) {
        configuration.setSavePeriod(savePeriod);
        getXiaomingBot().getFileSaver().readySave(configuration);
        user.sendMessage("{autoSavePeriodSetSuccessfully}");
    }

    @Filter(CommandWords.AUTO + CommandWords.OPTIMIZE + CommandWords.PERIOD)
    @Permission("config.optimize.period.look")
    public void onLookOptimizePeriod(XiaomingUser user) {
        user.setProperty("period", TimeUtility.toTimeLength(configuration.getOptimizePeriod()));
        user.sendMessage("{autoOptimizePeriodIs}");
    }

    @Filter(CommandWords.SET + CommandWords.AUTO + CommandWords.OPTIMIZE + CommandWords.PERIOD + " {period}")
    @Permission("config.optimize.period.set")
    public void onSetOptimizePeriod(XiaomingUser user, @FilterParameter("period") long savePeriod) {
        configuration.setOptimizePeriod(savePeriod);
        getXiaomingBot().getFileSaver().readySave(configuration);
        user.sendMessage("{autoOptimizePeriodSetSuccessfully}");
    }
}
