package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.GroupInteractor;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.config.Configuration;
import com.chuanwise.xiaoming.api.license.LicenseManager;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.response.ResponseGroupManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;
import com.chuanwise.xiaoming.core.response.ResponseGroupImpl;
import net.mamoe.mirai.contact.Group;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 全局指令处理器
 * @author Chuanwise
 */
public class GlobalCommandInteractor extends CommandInteractorImpl {
    public static final String ENABLE_TAG = "enable";

    public GlobalCommandInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        setExternalUse(true);
    }

    @Filter(CommandWords.USE_REGEX + CommandWords.XIAOMING_REGEX)
    @RequirePermission("enable")
    public void onUseXiaoming(XiaomingUser user) {
        final Configuration config = getXiaomingBot().getConfig();
        if (config.isEnableLicense()) {
            final LicenseManager licenceManager = getXiaomingBot().getLicenseManager();
            final long qq = user.getQQ();
            if (licenceManager.isAgreed(qq)) {
                user.sendMessage("你此前已经同意了小明使用协议");
            } else {
                user.sendPrivateMessage(getXiaomingBot().getTextManager().loadOrFail(config.getLicenseName()));
                user.sendPrivateMessage("如果你同意上述协议，请告诉我「同意」");

                final String nextInput = user.nextInput();
                if (Objects.equals(nextInput, "同意")) {
                    user.sendMessage("你已经可以使用小明了，未来记得遵守我们的约定");
                    licenceManager.agree(qq);
                } else {
                    user.sendMessage("如果未来希望使用小明，仍然可以告诉我「使用小明」");
                }
                getXiaomingBot().getRegularPreserveManager().readySave(licenceManager);
            }
        } else {
            user.sendMessage("不需要专门启动小明哦，小明为人民服务 {}", getXiaomingBot().getWordManager().get("happy"));
        }
    }

    @Filter(CommandWords.CANCEL_REGEX + CommandWords.USE_REGEX + CommandWords.XIAOMING_REGEX)
    @RequirePermission("disable")
    public void onCancelUseXiaoming(XiaomingUser user) {
        final Configuration config = getXiaomingBot().getConfig();
        if (config.isEnableLicense()) {
            final LicenseManager licenceManager = getXiaomingBot().getLicenseManager();
            final long qq = user.getQQ();
            if (licenceManager.isAgreed(qq)) {
                licenceManager.remove(qq);
                user.sendMessage("已取消使用小明。如果未来希望使用小明，仍然可以告诉我「使用小明」");
                getXiaomingBot().getRegularPreserveManager().readySave(licenceManager);
            } else {
                user.sendMessage("此前你并未同意《小明使用须知》");
            }
        } else {
            user.sendMessage("不需要专门取消小明哦，小明为人民服务 {}", getXiaomingBot().getWordManager().get("happy"));
        }
    }

    @GroupInteractor
    @Filter(CommandWords.THIS_REGEX + CommandWords.GROUP_REGEX + CommandWords.ENABLE_REGEX + CommandWords.XIAOMING_REGEX)
    @RequirePermission("group.enable")
    public void onEnableXiaoming(XiaomingUser user) {
        final Group group = user.getGroup();
        final ResponseGroupManager responseGroupManager = getXiaomingBot().getResponseGroupManager();

        ResponseGroup responseGroup = responseGroupManager.fromCode(group.getId());
        if (Objects.isNull(responseGroup)) {
            responseGroup = new ResponseGroupImpl(group.getId(), group.getName());
            responseGroupManager.addGroup(responseGroup);
            getXiaomingBot().getRegularPreserveManager().readySave(responseGroupManager);
        }

        if (responseGroup.hasTag(ENABLE_TAG)) {
            user.sendWarn("本群已经是小明的响应群了哦");
        } else {
            responseGroup.addTag(ENABLE_TAG);
            user.sendMessage("成功在本群启用小明 (๑•̀ㅂ•́)و✧");
            getXiaomingBot().getRegularPreserveManager().readySave(responseGroupManager);
        }
    }

    @GroupInteractor
    @Filter(CommandWords.THIS_REGEX + CommandWords.GROUP_REGEX + CommandWords.DISABLE_REGEX + CommandWords.XIAOMING_REGEX)
    @RequirePermission("group.disable")
    public void onDisableXiaoming(XiaomingUser user) {
        final Group group = user.getAsGroupMember().getGroup();
        final ResponseGroupManager responseGroupManager = getXiaomingBot().getResponseGroupManager();

        ResponseGroup responseGroup = responseGroupManager.fromCode(group.getId());
        if (Objects.isNull(responseGroup)) {
            user.sendMessage("本群还不是小明的响应群哦");
        }

        if (responseGroup.hasTag(ENABLE_TAG)) {
            responseGroup.removeTag(ENABLE_TAG);
            user.sendMessage("本群不再是小明的响应群啦。未来希望启动小明输入 #启动小明 就可以啦。");
        } else {
            user.sendWarn("本群曾是小明的响应群，但是现在还不是哦");
            getXiaomingBot().getRegularPreserveManager().readySave(responseGroupManager);
        }
    }
}
