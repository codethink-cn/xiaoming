package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.Require;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.configuration.Configuration;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CollectionUtils;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.InteractorUtils;
import com.chuanwise.xiaoming.api.util.TimeUtils;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;

import java.util.Objects;
import java.util.Set;

public class ConfigurationCommandInteractor extends CommandInteractorImpl {
    final Configuration configuration;

    public ConfigurationCommandInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        this.configuration = xiaomingBot.getConfiguration();
    }

    final static String CLEAR = "(明确|clear)";
    final static String DEBUG = "(维护|调试|debug)";
    final static String LICENSE = "(协议|license)";

    @Filter(CommandWords.ENABLE + DEBUG)
    @Require("debug.enable")
    public void onEnableDebug(XiaomingUser user) {
        if (configuration.isDebug()) {
            user.sendMessage("{debugModeAlreadyEnabled}");
        } else {
            configuration.setDebug(true);
            getXiaomingBot().getScheduler().readySave(configuration);
            user.sendMessage("{debugModeEnabledSuccessfully}");
        }
    }

    @Filter(CommandWords.DISABLE + DEBUG)
    @Require("debug.enable")
    public void onDisableDebug(XiaomingUser user) {
        if (configuration.isDebug()) {
            configuration.setDebug(false);
            getXiaomingBot().getScheduler().readySave(configuration);
            user.sendMessage("{debugModeDisabledSuccessfully}");
        } else {
            user.sendMessage("{debugModeHasNotBeenEnabled}");
        }
    }

    @Filter(CommandWords.ENABLE + CLEAR + CommandWords.CALL)
    @Require("config.clearcall.enable")
    public void onEnableClearCall(XiaomingUser user) {
        if (configuration.isEnableClearCall()) {
            user.sendMessage("{clearCallAlreadyEnabled}");
        } else {
            final Set<String> callPrefixs = configuration.getClearCallPrefixes();
            InteractorUtils.fillStringCollection(user,
                    user.replaceLanguage("theMessageBeginWithWitchElementsShouldBeNoticed"),
                    user.replaceLanguage("clearCallPrefixSet"), callPrefixs, false);
            configuration.setEnableClearCall(true);

            user.setProperty("set", callPrefixs);
            user.sendMessage("{iWillOnlyMentionMessageStartsWithTheElementInThisSet}");
            getXiaomingBot().getScheduler().readySave(getXiaomingBot().getConfiguration());
        }
    }

    @Filter(CLEAR + CommandWords.CALL)
    @Require("config.clearcall.look")
    public void onListClearCallPrefix(XiaomingUser user) {
        if (configuration.isEnableClearCall()) {
            final Set<String> clearCallPrefixes = configuration.getClearCallPrefixes();
            if (clearCallPrefixes.isEmpty()) {
                user.sendMessage("当前具备 " + configuration.getClearCallGroupTag() + " 标记的群内启动了明确调用，但还没有设置任何消息开头的格式");
            } else {
                user.sendMessage("当前具备 " + configuration.getClearCallGroupTag() + " 标记的群内启动了明确调用，消息开头必须为 " +
                        CollectionUtils.getSummary(clearCallPrefixes, String::toString, "", "", "、") + " 当中的一个");
            }
        } else {
            user.sendMessage("{clearCallHasNotBeenEnabled}");
        }
    }

    @Filter(CommandWords.SET + CLEAR + CommandWords.CALL + CommandWords.MESSAGE + CommandWords.HEAD)
    @Filter(CommandWords.EDIT + CLEAR + CommandWords.CALL + CommandWords.MESSAGE + CommandWords.HEAD)
    @Require("config.clearcall.head.set")
    public void onSetClearCallPrefix(XiaomingUser user) {
        final Set<String> clearCallPrefixes = configuration.getClearCallPrefixes();
        clearCallPrefixes.clear();
        InteractorUtils.fillStringCollection(user, "以什么开头的消息需要被小明注意到呢", "明确调用消息头", clearCallPrefixes, false);

        getXiaomingBot().getScheduler().readySave(configuration);
        user.sendMessage("成功修改明确调用消息头为：" + CollectionUtils.getSummary(clearCallPrefixes, String::toString, "", "", "、"));
    }

    @Filter(CommandWords.DISABLE + CLEAR + CommandWords.CALL)
    @Require("config.clearcall.disable")
    public void onDisableClearCall(XiaomingUser user) {
        if (configuration.isEnableClearCall()) {
            configuration.setEnableClearCall(false);
            getXiaomingBot().getScheduler().readySave(configuration);
            user.sendMessage("{clearCallDisabledSuccessfully}");
        } else {
            user.sendMessage("{clearCallHasNotBeenEnabled}");
        }
    }

    @Filter(CommandWords.ENABLE + CommandWords.USE + LICENSE)
    @Require("config.license.enable")
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
            getXiaomingBot().getScheduler().readySave(config);
        }
    }

    @Filter(CommandWords.USE + LICENSE)
    @Require("config.license.look")
    public void onLookCompulsoryAgreement(XiaomingUser user) {
        final Configuration config = getXiaomingBot().getConfiguration();
        if (config.isEnableLicense()) {
            user.sendMessage(getXiaomingBot().getLanguage().getString("license"));
        } else {
            user.sendMessage("{compulsoryAgreementHasNotBeenEnabled}");
        }
    }

    @Filter(CommandWords.DISABLE + CommandWords.USE + LICENSE)
    @Require("config.license.disable")
    public void onDisableCompulsoryAgreement(XiaomingUser user) {
        final Configuration config = getXiaomingBot().getConfiguration();
        if (config.isEnableLicense()) {
            config.setEnableLicense(false);
            getXiaomingBot().getScheduler().readySave(config);
            user.sendMessage("{disableCompulsoryAgreementSuccessfully}");
        } else {
            user.sendMessage("{compulsoryAgreementHasNotBeenEnabled}");
        }
    }

    @Filter(CommandWords.SAVE + CommandWords.PERIOD)
    @Require("config.save.period.look")
    public void onLookSavePeriod(XiaomingUser user) {
        user.setProperty("period", TimeUtils.toTimeString(configuration.getSavePeriod()));
        user.sendMessage("{autoSavePeriodIs}");
    }

    @Filter(CommandWords.SET + CommandWords.SAVE + CommandWords.PERIOD + " {period}")
    @Require("config.save.period.set")
    public void onSetSavePeriod(XiaomingUser user, @FilterParameter("period") long savePeriod) {
        configuration.setSavePeriod(savePeriod);
        getXiaomingBot().getScheduler().readySave(configuration);
        user.sendMessage("{autoSavePeriodSetSuccessfully}");
    }

    @Filter(CommandWords.AUTO + CommandWords.OPTIMIZE + CommandWords.PERIOD)
    @Require("config.optimize.period.look")
    public void onLookOptimizePeriod(XiaomingUser user) {
        user.setProperty("period", TimeUtils.toTimeString(configuration.getOptimizePeriod()));
        user.sendMessage("{autoOptimizePeriodIs}");
    }

    @Filter(CommandWords.SET + CommandWords.AUTO + CommandWords.OPTIMIZE + CommandWords.PERIOD + " {period}")
    @Require("config.optimize.period.set")
    public void onSetOptimizePeriod(XiaomingUser user, @FilterParameter("period") long savePeriod) {
        configuration.setOptimizePeriod(savePeriod);
        getXiaomingBot().getScheduler().readySave(configuration);
        user.sendMessage("{autoOptimizePeriodSetSuccessfully}");
    }
}
