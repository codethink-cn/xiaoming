package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.account.AccountManager;
import com.chuanwise.xiaoming.api.account.record.Record;
import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.Require;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.AtUtils;
import com.chuanwise.xiaoming.api.util.CollectionUtils;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.InteractorUtils;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

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
        final String emptyHistory = "{userHasNoHistory}";
        if (Objects.isNull(account)) {
            user.sendWarning(emptyHistory);
            return;
        }
        InteractorUtils.showCollection(user, account.getCommands(), Record::getMessage, emptyHistory, 5);
    }

    @Filter(CommandWords.UNBLOCK + " {plugin}")
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
                getXiaomingBot().getScheduler().readySave(account);
                user.sendMessage("成功取消屏蔽了插件：{}", plugin);
            } else {
                user.sendError("你不能使用插件：{}", plugin);
            }
        } else {
            user.sendError("你还没有屏蔽插件：{}", plugin);
        }
    }

    @Filter(CommandWords.BLOCK + " {plugin}")
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
    @Filter(CommandWords.SET + CommandWords.ALIAS + " {qq} {alias}")
    @Require("account.user.alias")
    public void onSetUserAlias(XiaomingUser user,
                               @FilterParameter("qq") long qq,
                               @FilterParameter("alias") String alias) {
        final Account account = accountManager.getOrPutAccount(qq);
        account.setAlias(alias);
        user.sendMessage("成功将该用户的备注设置为{}", alias);
        getXiaomingBot().getScheduler().readySave(account);
    }

    @Filter(CommandWords.TAG + " {qq} {tag}")
    @Require("account.user.tag.add")
    public void onAddUserTag(XiaomingUser user,
                               @FilterParameter("qq") Account account,
                               @FilterParameter("tag") String tag) {
        if (account.hasTag(tag)) {
            user.sendError("该用户已经有这个标记了");
        } else {
            account.addTag(tag);
            user.sendMessage("成功为该用户添加了标记「{tag}」");
            getXiaomingBot().getScheduler().readySave(account);
        }
    }

    @Filter(CommandWords.REMOVE + CommandWords.TAG + " {qq} {tag}")
    @Require("account.user.tag.add")
    public void onRemoveUserTag(XiaomingUser user,
                               @FilterParameter("qq") Account account,
                               @FilterParameter("tag") String tag) {
        if (Arrays.asList("recorded", user.getCodeString()).contains(tag)) {
            user.sendError("「{tag}」是原生标记，不可以删除");
            return;
        }
        if (account.hasTag(tag)) {
            account.removeTag(tag);
            user.sendMessage("成功删除了该用户的标记「{tag}」");
            getXiaomingBot().getScheduler().readySave(account);
        } else {
            user.sendError("该用户并没有这个 tag 哦");
        }
    }

    @Filter(CommandWords.TAG + " {qq}")
    @Require("account.user.tag.add")
    public void onRemoveUserTag(XiaomingUser user,
                               @FilterParameter("qq") Account account) {
        final Set<String> tags = account.getTags();
        if (tags.isEmpty()) {
            user.sendError("该用户没有任何标记");
        } else {
            user.sendMessage("该用户的标记有：" + CollectionUtils.getSummary(tags, String::toString, "", "", "、"));
        }
    }

    @Override
    public <T> Object onParameter(XiaomingUser user, Class<T> clazz, String parameterName, String currentValue, String defaultValue) {
        final Object result = super.onParameter(user, clazz, parameterName, currentValue, defaultValue);
        if (Objects.nonNull(result)) {
            return result;
        }
        if (clazz.isAssignableFrom(Account.class) && Objects.equals("qq", currentValue)) {
            final long qq = AtUtils.parseQQ(currentValue);
            if (qq == -1) {
                user.sendError("「{qq}」并不是一个合理的 QQ 哦");
                return null;
            } else {
                return getXiaomingBot().getAccountManager().getOrPutAccount(qq);
            }
        }
        return null;
    }
}
