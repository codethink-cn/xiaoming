package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.account.AccountManager;
import cn.chuanwise.xiaoming.account.record.Record;
import cn.chuanwise.xiaoming.annotation.Customizable;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.plugin.XiaomingPlugin;
import cn.chuanwise.xiaoming.tag.TagHolder;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.utility.CommandWords;
import cn.chuanwise.xiaoming.utility.InteractorUtility;
import cn.chuanwise.xiaoming.interactor.InteractorImpl;

import java.util.*;

/**
 * 和用户账号相关的指令处理器
 * @author Chuanwise
 */
public class AccountInteractor extends InteractorImpl {
    final AccountManager accountManager;
    static final String HISTORY = "(历史|history)";

    public AccountInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        accountManager = getXiaomingBot().getAccountManager();
        setUsageCommandFormat(CommandWords.ACCOUNT + CommandWords.HELP);
    }

    @Customizable("listHistory")
    @Filter(HISTORY + " {qq}")
    @Permission("account.history")
    public void onListUserHistory(XiaomingUser user,
                                  @FilterParameter("qq") long qq) {
        final Account account = user.getAccount();
        final List<Record> histories = account.getHistories();
        if (CollectionUtility.isEmpty(histories)) {
            user.sendWarning("{lang.userHasNoHistory}");
        } else {
            InteractorUtility.showCollection(user, account.getCommands(), Record::getMessage, 5);
        }
    }

    @Customizable("unblockPlugin")
    @Filter(CommandWords.UNBLOCK + " {plugin}")
    @Permission("account.plugin.unblock")
    public void onUnblockPlugin(XiaomingUser user,
                                @FilterParameter("plugin") XiaomingPlugin plugin) {
        final Account account = user.getAccount();
        if (account.isBlockPlugin(plugin)) {
            if (user.hasPermission("use." + plugin)) {
                account.unblockPlugin(plugin);
                getXiaomingBot().getFileSaver().readyToSave(account);
                user.sendMessage("{lang.userUnblockPluginSuccessfully}", plugin);
            } else {
                user.sendError("{lang.userCanNotUsePlugin}", plugin);
            }
        } else {
            user.sendError("{lang.userHadNotEnablePlugin}", plugin);
        }
    }

    @Customizable("blockPlugin")
    @Filter(CommandWords.BLOCK + " {plugin}")
    @Permission("account.plugin.block")
    public void onBlockPlugin(XiaomingUser user,
                              @FilterParameter("plugin") XiaomingPlugin plugin) {
        final Account account = accountManager.forAccount(user.getCode());
        if (account.isBlockPlugin(plugin)) {
            user.sendError("{lang.userHadBlockedThePlugin}", plugin);
        } else {
            account.blockPlugin(plugin);
            getXiaomingBot().getFileSaver().readyToSave(account);
            user.sendMessage("{lang.userBlockPluginSuccessfully}", plugin);
        }
    }

    @Customizable("setUserAlias")
    @Filter(CommandWords.ALIAS + " {qq} {remain}")
    @Filter(CommandWords.SET + CommandWords.ALIAS + " {qq} {remain}")
    @Permission("account.user.alias.set")
    public void onSetUserAlias(XiaomingUser user,
                               @FilterParameter("qq") long qq,
                               @FilterParameter("remain") String alias) {
        final Account account = accountManager.forAccount(qq);
        account.setAlias(alias);
        user.sendMessage("{lang.aliasSetSuccessfully}", alias);
        getXiaomingBot().getFileSaver().readyToSave(account);
    }

    @Customizable("lookUserAlias")
    @Filter(CommandWords.ALIAS + " {qq}")
    @Permission("account.user.alias.look")
    public void onSetUserAlias(XiaomingUser user,
                               @FilterParameter("qq") long qq) {
        user.sendMessage("{lang.aliasIs}", qq);
    }

    @Customizable("addUserTag")
    @Filter(CommandWords.TAG + " {qq} {tag}")
    @Permission("account.user.tag.add")
    public void onAddUserTag(XiaomingUser user,
                             @FilterParameter("qq") Account account,
                             @FilterParameter("tag") String tag) {
        if (account.hasTag(tag)) {
            user.sendError("{lang.userAlreadyHasTag}", tag);
        } else {
            account.addTag(tag);
            user.sendMessage("{lang.userTagAddSuccessfully}", tag);
            getXiaomingBot().getFileSaver().readyToSave(account);
        }
    }

    @Customizable("removeUserTag")
    @Filter(CommandWords.REMOVE + CommandWords.TAG + " {qq} {tag}")
    @Permission("account.user.tag.add")
    public void onRemoveUserTag(XiaomingUser user,
                                @FilterParameter("qq") Account account,
                                @FilterParameter("tag") String tag) {
        if (Arrays.asList(TagHolder.RECORDED, user.getCodeString()).contains(tag)) {
            user.sendError("{lang.canNotRemoveOriginalTag}", tag);
            return;
        }
        if (account.hasTag(tag)) {
            account.removeTag(tag);
            user.sendMessage("{lang.userTagRemoveSuccessfully}", tag);
            getXiaomingBot().getFileSaver().readyToSave(account);
        } else {
            user.sendError("{lang.userHadNotTheTag}", tag);
        }
    }

    @Customizable("listUserTag")
    @Filter(CommandWords.TAG + " {qq}")
    @Permission("account.user.tag.list")
    public void onListUserTag(XiaomingUser user,
                              @FilterParameter("qq") long qq) {
        final Set<String> tags = getXiaomingBot().getAccountManager().getTags(qq);
        user.sendMessage("{lang.userTagsAreAsFollows}");
    }
}