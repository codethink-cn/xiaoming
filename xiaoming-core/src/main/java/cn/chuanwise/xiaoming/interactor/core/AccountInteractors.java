package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.account.AccountManager;
import cn.chuanwise.xiaoming.account.record.Record;
import cn.chuanwise.xiaoming.annotation.Name;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.tag.TagHolder;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.utility.CommandWords;
import cn.chuanwise.xiaoming.utility.InteractorUtility;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;

import java.util.*;

/**
 * 和用户账号相关的指令处理器
 * @author Chuanwise
 */
public class AccountInteractors extends SimpleInteractors {
    AccountManager accountManager;

    @Override
    public void onRegister() {
        accountManager = getXiaomingBot().getAccountManager();
    }

    @Name("listHistory")
    @Filter(CommandWords.HISTORY + " {qq}")
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

    @Name("unblockPlugin")
    @Filter(CommandWords.UNBLOCK + " {插件名}")
    @Permission("account.plugin.unblock")
    public void onUnblockPlugin(XiaomingUser user,
                                @FilterParameter("插件名") Plugin plugin) {
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

    @Name("blockPlugin")
    @Filter(CommandWords.BLOCK + " {插件名}")
    @Permission("account.plugin.block")
    public void onBlockPlugin(XiaomingUser user,
                              @FilterParameter("插件名") Plugin plugin) {
        final Account account = accountManager.getAccount(user.getCode());
        if (account.isBlockPlugin(plugin)) {
            user.sendError("{lang.userHadBlockedThePlugin}", plugin);
        } else {
            account.blockPlugin(plugin);
            getXiaomingBot().getFileSaver().readyToSave(account);
            user.sendMessage("{lang.userBlockPluginSuccessfully}", plugin);
        }
    }

    @Name("setUserAlias")
    @Filter(CommandWords.ALIAS + " {qq} {r:备注}")
    @Filter(CommandWords.SET + CommandWords.ALIAS + " {qq} {r:备注}")
    @Permission("account.user.alias.set")
    public void onSetUserAlias(XiaomingUser user,
                               @FilterParameter("qq") long qq,
                               @FilterParameter("备注") String alias) {
        final Account account = accountManager.getAccount(qq);
        account.setAlias(alias);
        user.sendMessage("{lang.aliasSetSuccessfully}", alias);
        getXiaomingBot().getFileSaver().readyToSave(account);
    }

    @Name("lookUserAlias")
    @Filter(CommandWords.ALIAS + " {qq}")
    @Permission("account.user.alias.look")
    public void onSetUserAlias(XiaomingUser user,
                               @FilterParameter("qq") long qq) {
        user.sendMessage("{lang.aliasIs}", qq);
    }

    @Name("addUserTag")
    @Filter(CommandWords.TAG + " {qq} {标记}")
    @Permission("account.user.tag.add")
    public void onAddUserTag(XiaomingUser user,
                             @FilterParameter("qq") Account account,
                             @FilterParameter("标记") String tag) {
        if (account.hasTag(tag)) {
            user.sendError("{lang.userAlreadyHasTag}", tag);
        } else {
            account.addTag(tag);
            user.sendMessage("{lang.userTagAddSuccessfully}", tag);
            getXiaomingBot().getFileSaver().readyToSave(account);
        }
    }

    @Name("removeUserTag")
    @Filter(CommandWords.REMOVE + CommandWords.TAG + " {qq} {标签}")
    @Permission("account.user.tag.add")
    public void onRemoveUserTag(XiaomingUser user,
                                @FilterParameter("qq") Account account,
                                @FilterParameter("标签") String tag) {
        if (account.isOriginalTag(tag)) {
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

    @Name("listUserTag")
    @Filter(CommandWords.TAG + " {qq}")
    @Permission("account.user.tag.list")
    public void onListUserTag(XiaomingUser user,
                              @FilterParameter("qq") long qq) {
        final Set<String> tags = getXiaomingBot().getAccountManager().getTags(qq);
        user.sendMessage("{lang.userTagsAreAsFollows}");
    }
}