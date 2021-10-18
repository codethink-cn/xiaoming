package cn.chuanwise.xiaoming.permission;

import cn.chuanwise.util.ConditionUtil;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.group.GroupInformation;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.RegisterUtil;
import lombok.Getter;

@Getter
public class PermissionServiceImpl
        extends ModuleObjectImpl
        implements PermissionService {
    Plugin plugin;

    PermissionRequester permissionRequester;

    final CorePermissionRequester corePermissionRequester;

    public PermissionServiceImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
        corePermissionRequester = new SimpleCorePermissionRequester();
        reset();
    }

    @Override
    public void register(PermissionRequester requester, Plugin plugin) {
        final String objectName = "permission requester";
        ConditionUtil.notNull(requester, objectName);
        RegisterUtil.checkRegister(xiaomingBot, plugin, objectName);

        this.permissionRequester = requester;
        this.plugin = plugin;
    }

    @Override
    public void reset() {
        permissionRequester = corePermissionRequester;
        plugin = null;
    }

    @Override
    public boolean hasPermission(XiaomingUser user, String permission) {
        // if a user is console xiaoming user,
        // he must has permission
        if (user instanceof ConsoleXiaomingUser) {
            return true;
        }

        // if user is banned, he hasn't permission
        final Account account = user.getAccount();
        return !account.isBanned() && permissionRequester.hasPermission(user, permission);
    }

    @Override
    public boolean hasPermission(Account account, String permission) {
        // bot itself must has permission
        if (account.getCode() == xiaomingBot.getCode()) {
            return true;
        }

        return !account.isBanned() && permissionRequester.hasPermission(account, permission);
    }

    @Override
    public boolean hasPermission(Account account, GroupInformation groupInformation, String permission) {
        // bot itself must has permission
        if (account.getCode() == xiaomingBot.getCode()) {
            return true;
        }

        return !account.isBanned() && permissionRequester.hasPermission(account, groupInformation, permission);
    }

    @Override
    public boolean hasPermission(long userCode, long groupCode, String permissionNode) {
        final XiaomingBot xiaomingBot = getXiaomingBot();
        final Account account = xiaomingBot.getAccountManager().createAccount(userCode);

        return hasPermission(account, xiaomingBot.getGroupInformationManager().addGroupInformation(groupCode), permissionNode);
    }
}
