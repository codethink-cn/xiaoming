package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.account.AccountManager;
import cn.chuanwise.xiaoming.annotation.Name;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;

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

    @Filter(CommandWords.LET + CommandWords.ADMINISTRATOR + " {qq}")
    @Filter(CommandWords.LET + CommandWords.OPERATOR + " {qq}")
    @Filter(CommandWords.OPERATOR + " {qq}")
    @Permission("account.user.administrator.grant")
    public void grantAdministrator(XiaomingUser user, @FilterParameter("qq") long qq) {
        final Account account = accountManager.createAccount(qq);

        if (account.isBanned()) {
            user.sendError("该用户已被封禁");
        } else if (account.isAdministrator()) {
            user.sendMessage("该用户已经是管理员了");
        } else {
            account.setAdministrator(true);
            xiaomingBot.getFileSaver().readyToSave(accountManager);

            user.sendMessage("成功授予" + account.getAliasAndCode() + "管理员权限");
        }
    }

    @Filter(CommandWords.REVOKE + CommandWords.ADMINISTRATOR + " {qq}")
    @Filter(CommandWords.REVOKE + CommandWords.OPERATOR + " {qq}")
    @Filter(CommandWords.CANCEL + CommandWords.ADMINISTRATOR + " {qq}")
    @Filter(CommandWords.CANCEL + CommandWords.OPERATOR + " {qq}")
    @Permission("account.user.administrator.revoke")
    public void revokeAdministrator(XiaomingUser user, @FilterParameter("qq") long qq) {
        final Account account = accountManager.createAccount(qq);

        if (account.isBanned()) {
            user.sendError("该用户已被封禁");
        } else if (account.isAdministrator()) {
            account.setAdministrator(false);
            xiaomingBot.getFileSaver().readyToSave(accountManager);
            user.sendMessage("成功收回授予" + account.getAliasAndCode() + "的管理员权限");
        } else {
            user.sendMessage("该用户并不是管理员");
        }
    }

    @Filter(CommandWords.BAN + " {qq}")
    @Permission("account.user.ban")
    public void banUser(XiaomingUser user, @FilterParameter("qq") long qq) {
        final Account account = accountManager.createAccount(qq);

        if (account.isBanned()) {
            user.sendError("该用户已被封禁");
        } else {
            account.setBanned(true);
            xiaomingBot.getFileSaver().readyToSave(accountManager);
            user.sendMessage("成功封禁" + account.getAliasAndCode());
        }
    }

    @Filter(CommandWords.UNBAN + " {qq}")
    @Permission("account.user.unban")
    public void unbanUser(XiaomingUser user, @FilterParameter("qq") long qq) {
        final Account account = accountManager.createAccount(qq);

        if (account.isBanned()) {
            account.setBanned(false);
            xiaomingBot.getFileSaver().readyToSave(accountManager);
            user.sendMessage("成功解禁" + account.getAliasAndCode());
        } else {
            user.sendError("该用户并未被封禁");
        }
    }

    @Filter(CommandWords.ALIAS + " {qq} {r:备注}")
    @Filter(CommandWords.SET + CommandWords.ALIAS + " {qq} {r:备注}")
    @Permission("account.user.alias.set")
    public void setUserAlias(XiaomingUser user,
                             @FilterParameter("qq") long qq,
                             @FilterParameter("备注") String alias) {
        final Account account = accountManager.createAccount(qq);
        account.setAlias(alias);
        user.sendMessage("{lang.aliasSetSuccessfully}", alias);
        getXiaomingBot().getFileSaver().readyToSave(accountManager);
    }

    @Filter(CommandWords.ALIAS + " {qq}")
    @Permission("account.user.alias.look")
    public void lookUserAlias(XiaomingUser user,
                              @FilterParameter("qq") long qq) {
        user.sendMessage("{lang.aliasIs}", qq);
    }

    @Name("addUserTag")
    @Filter(CommandWords.TAG + " {qq} {标记}")
    @Permission("account.user.tag.add")
    public void addUserTag(XiaomingUser user,
                           @FilterParameter("qq") Account account,
                           @FilterParameter("标记") String tag) {
        if (account.hasTag(tag)) {
            user.sendError("{lang.userAlreadyHasTag}", tag);
        } else {
            account.addTag(tag);
            user.sendMessage("{lang.userTagAddSuccessfully}", tag);
            getXiaomingBot().getFileSaver().readyToSave(accountManager);
        }
    }

    @Name("removeUserTag")
    @Filter(CommandWords.REMOVE + CommandWords.TAG + " {qq} {标签}")
    @Permission("account.user.tag.add")
    public void removeUserTag(XiaomingUser user,
                              @FilterParameter("qq") Account account,
                              @FilterParameter("标签") String tag) {
        if (account.isOriginalTag(tag)) {
            user.sendError("{lang.canNotRemoveOriginalTag}", tag);
            return;
        }
        if (account.hasTag(tag)) {
            account.removeTag(tag);
            user.sendMessage("{lang.userTagRemoveSuccessfully}", tag);
            getXiaomingBot().getFileSaver().readyToSave(accountManager);
        } else {
            user.sendError("{lang.userHadNotTheTag}", tag);
        }
    }

    @Name("listUserTag")
    @Filter(CommandWords.TAG + " {qq}")
    @Permission("account.user.tag.list")
    public void listUserTag(XiaomingUser user,
                            @FilterParameter("qq") long qq) {
        user.sendMessage("{lang.userTagsAreAsFollows}");
    }
}