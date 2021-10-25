package cn.chuanwise.xiaoming.interactor.interactors;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.account.AccountManager;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Required;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;

import java.util.List;

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
    @Required("account.user.administrator.grant")
    public void op(XiaomingUser user, @FilterParameter("qq") long qq) {
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
    @Filter(CommandWords.DEOPERATOR + " {qq}")
    @Required("account.user.administrator.revoke")
    public void deop(XiaomingUser user, @FilterParameter("qq") long qq) {
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
    @Required("account.user.ban")
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
    @Required("account.user.unban")
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
    @Required("account.user.alias.set")
    public void setUserAlias(XiaomingUser user,
                             @FilterParameter("qq") long qq,
                             @FilterParameter("备注") String alias) {
        final Account account = accountManager.createAccount(qq);
        account.setAlias(alias);
        user.sendMessage("{lang.aliasSetSuccessfully}", alias);
        getXiaomingBot().getFileSaver().readyToSave(accountManager);
    }

    @Filter(CommandWords.ALIAS + " {qq}")
    @Required("account.user.alias.look")
    public void lookUserAlias(XiaomingUser user,
                              @FilterParameter("qq") long qq) {
        user.sendMessage("{lang.aliasIs}", qq);
    }

    @Filter(CommandWords.TAG + " {qq} {标记}")
    @Required("account.user.tag.add")
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

    @Filter(CommandWords.REMOVE + CommandWords.TAG + " {qq} {标签}")
    @Required("account.user.tag.add")
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

    @Filter(CommandWords.TAGGED + CommandWords.ACCEPT + " {r:标签}")
    @Filter(CommandWords.TAGGED + CommandWords.USER + " {r:标签}")
    @Required("account.tag.search")
    public void searchAccountsByTag(XiaomingUser user, @FilterParameter("标签") String tag) {
        final List<Account> informations = accountManager.searchAccountsByTag(tag);
        if (informations.isEmpty()) {
            user.sendError("没有用「" + tag + "」找到任何账户");
        } else {
            user.sendMessage("用「" + tag + "」找到下列用户：\n" +
                    CollectionUtil.toIndexString(informations, Account::getAliasAndCode));
        }
    }
}