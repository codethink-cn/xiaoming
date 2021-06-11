package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.Require;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.configuration.Configuration;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.InteractorUtils;
import com.chuanwise.xiaoming.api.util.TimeUtils;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;
import com.sun.media.jfxmedia.logging.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;

public class ConfirationCommandInteractor extends CommandInteractorImpl {
    final Configuration configuration;

    public ConfirationCommandInteractor(XiaomingBot xiaomingBot) {
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
            final Set<String> callPrefixs = configuration.getCallPrefixs();
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
            user.setProperty("set", configuration.getCallPrefixs());
            user.sendMessage("{onlyTheMessageBeginWithTheElementInThisSetWillBeNoticed}");
        } else {
            user.sendMessage("{clearCallHasNotBeenEnabled}");
        }
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
            String license = getXiaomingBot().getLanguageManager().getStringOrDefault("license", null);
            if (Objects.isNull(license)) {
                user.sendMessage("{inputLicense}");
                license = user.nextInput().serialize();
                getXiaomingBot().getLanguageManager().put("license", license);
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
            user.sendMessage(getXiaomingBot().getLanguageManager().getString("license"));
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
