package com.chuanwise.xiaoming.core.command.executor;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.account.AccountManager;
import com.chuanwise.xiaoming.api.annotation.Command;
import com.chuanwise.xiaoming.api.annotation.CommandParameter;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;

/**
 * 和用户账号相关的指令处理器
 * @author Chuanwise
 */
public class AccountCommandExecutor extends CommandExecutorImpl {
    final AccountManager accountManager;

    public AccountCommandExecutor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        accountManager = getXiaomingBot().getAccountManager();
    }

    @Override
    public String usageStringsPrefix() {
        return CommandWords.ACCOUNT_REGEX;
    }

    @Command(CommandWords.ACCOUNT_REGEX + " " + CommandWords.UNBLOCK_REGEX + " {plugin}")
    @RequirePermission("account.plugin.unblock")
    public void onUnblockPlugin(final GroupXiaomingUser user,
                                @CommandParameter("plugin") final String plugin) {
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

    @Command(CommandWords.ACCOUNT_REGEX + " " + CommandWords.BLOCK_REGEX + " {plugin}")
    @RequirePermission("account.plugin.block")
    public void onBlockPlugin(final XiaomingUser user,
                              @CommandParameter("plugin") final String plugin) {
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
}
