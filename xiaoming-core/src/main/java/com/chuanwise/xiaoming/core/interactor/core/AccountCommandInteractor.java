package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.account.AccountEvent;
import com.chuanwise.xiaoming.api.account.AccountManager;
import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.Require;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.InteractorUtils;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;

import java.util.Objects;

/**
 * 和用户账号相关的指令处理器
 * @author Chuanwise
 */
public class AccountCommandInteractor extends CommandInteractorImpl {
    final AccountManager accountManager;
    static final String HISTORY = "(历史|history)";

    public AccountCommandInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        accountManager = getXiaomingBot().getAccountManager();
        enableUsageCommand(CommandWords.ACCOUNT);
    }

    @Filter(HISTORY + " {qq}")
    @Require("account.history")
    public void onLookUserHistory(XiaomingUser user, @FilterParameter("qq") long qq) {
        final Account account = getXiaomingBot().getAccountManager().getAccount(qq);
        final String emptyHistory = "该用户没有任何历史记录";
        if (Objects.isNull(account)) {
            user.sendWarning(emptyHistory);
            return;
        }
        InteractorUtils.showList(user, account.getCommands(), AccountEvent::getMessage, emptyHistory, 5);
    }

    @Filter(CommandWords.ACCOUNT + " " + CommandWords.UNBLOCK + " {plugin}")
    @Require("account.plugin.unblock")
    public void onUnblockPlugin(XiaomingUser user,
                                @FilterParameter("plugin") String plugin) {
        final Account account = accountManager.getOrPutAccount(user.getCode());
        if (account.isBlockPlugin(plugin)) {
            if (getXiaomingBot().getPluginManager().isLoaded(plugin)) {
                user.sendError("小明没有加载插件：{}", plugin);
            }
            if (user.hasPermission("use." + plugin)) {
                account.unblockPlugin(plugin);
                getXiaomingBot().getFinalizer().readySave(account);
                user.sendMessage("成功取消屏蔽了插件：{}", plugin);
            } else {
                user.sendError("你不能使用插件：{}", plugin);
            }
        } else {
            user.sendError("你还没有屏蔽插件：{}", plugin);
        }
    }

    @Filter(CommandWords.ACCOUNT + " " + CommandWords.BLOCK + " {plugin}")
    @Require("account.plugin.block")
    public void onBlockPlugin(XiaomingUser user,
                              @FilterParameter("plugin") String plugin) {
        final Account account = accountManager.getOrPutAccount(user.getCode());
        if (account.isBlockPlugin(plugin)) {
            user.sendError("你已经屏蔽了插件：{}", plugin);
        } else {
            if (getXiaomingBot().getPluginManager().isLoaded(plugin)) {
                user.sendError("小明没有加载插件：{}", plugin);
            }
            account.blockPlugin(plugin);
            user.sendError("成功屏蔽了插件：{}", plugin);
        }
    }

    @Filter(CommandWords.ALIAS + " {qq} {alias}")
    @Require("account.user.alias")
    public void onSetUserAlias(XiaomingUser user,
                               @FilterParameter("qq") long qq,
                               @FilterParameter("alias") String alias) {
        final Account account = accountManager.getOrPutAccount(qq);
        account.setAlias(alias);
        user.sendMessage("成功将该用户的备注设置为{}", alias);
        getXiaomingBot().getFinalizer().readySave(account);
    }
}
