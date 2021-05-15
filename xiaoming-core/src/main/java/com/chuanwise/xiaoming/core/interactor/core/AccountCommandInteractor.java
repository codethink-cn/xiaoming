package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.account.AccountManager;
import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.GroupInteractor;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;

/**
 * 和用户账号相关的指令处理器
 * @author Chuanwise
 */
public class AccountCommandInteractor extends CommandInteractorImpl {
    final AccountManager accountManager;

    public AccountCommandInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        accountManager = getXiaomingBot().getAccountManager();
        enableUsageCommand(CommandWords.ACCOUNT_REGEX);
    }

    @GroupInteractor
    @Filter(CommandWords.ACCOUNT_REGEX + " " + CommandWords.UNBLOCK_REGEX + " {plugin}")
    @RequirePermission("account.plugin.unblock")
    public void onUnblockPlugin(XiaomingUser user,
                                @FilterParameter("plugin") String plugin) {
        final Account account = accountManager.getOrPutAccount(user.getQQ());
        if (account.isBlockPlugin(plugin)) {
            if (getXiaomingBot().getPluginManager().isLoaded(plugin)) {
                user.sendError("小明没有加载插件：{}", plugin);
            }
            if (user.hasPermission("use." + plugin)) {
                account.getBlockPlugins().remove(plugin);
                user.sendMessage("成功取消屏蔽了插件：{}", plugin);
            } else {
                user.sendError("你不能使用插件：{}", plugin);
            }
        } else {
            user.sendError("你还没有屏蔽插件：{}", plugin);
        }
    }

    @Filter(CommandWords.ACCOUNT_REGEX + " " + CommandWords.BLOCK_REGEX + " {plugin}")
    @RequirePermission("account.plugin.block")
    public void onBlockPlugin(XiaomingUser user,
                              @FilterParameter("plugin") String plugin) {
        final Account account = accountManager.getOrPutAccount(user.getQQ());
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

    @Filter(CommandWords.ALIAS_REGEX + " {qq} {alias}")
    @RequirePermission("account.user.alias")
    public void onSetUserAlias(XiaomingUser user,
                               @FilterParameter("qq") long qq,
                               @FilterParameter("alias") String alias) {
        final Account account = accountManager.getOrPutAccount(qq);
        account.setAlias(alias);
        user.sendMessage("成功将该用户的备注设置为{}", alias);
        getXiaomingBot().getRegularPreserveManager().readySave(account);
    }
}
