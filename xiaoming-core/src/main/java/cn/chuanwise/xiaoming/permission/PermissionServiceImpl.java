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

import java.io.File;
import java.util.Objects;

@Getter
public class PermissionServiceImpl
        extends ModuleObjectImpl
        implements PermissionService {
    Plugin plugin;

    PermissionRequester permissionRequester;

    final CorePermissionRequester corePermissionRequester;

    public PermissionServiceImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);

        final SimpleCorePermissionRequester simpleCorePermissionRequester = xiaomingBot
                .getFileLoader()
                .loadOrSupply(SimpleCorePermissionRequester.class,
                        new File(xiaomingBot.getConfigurationDirectory(), "core-permission-service.json"),
                        SimpleCorePermissionRequester::new);
        this.corePermissionRequester = simpleCorePermissionRequester;
        simpleCorePermissionRequester.setXiaomingBot(xiaomingBot);

        this.permissionRequester = this.corePermissionRequester;
        this.plugin = null;
    }

    @Override
    public boolean register(PermissionRequester requester, Plugin plugin) {
        final String objectName = "permission requester";
        ConditionUtil.notNull(requester, objectName);
        RegisterUtil.checkRegister(xiaomingBot, plugin, objectName);

        return register0(requester, plugin);
    }

    @Override
    public boolean reset() {
        return register0(corePermissionRequester, null);
    }

    protected boolean register0(PermissionRequester permissionRequester, Plugin plugin) {
        if (permissionRequester == this.permissionRequester) {
            return false;
        }

        try {
            this.permissionRequester.onDisable();
        } catch (Throwable throwable) {
            getLogger().error("关闭由" + Plugin.getChineseName(plugin) + "提供的权限请求器时出现异常", throwable);
        }

        this.permissionRequester = permissionRequester;
        this.plugin = plugin;

        try {
            this.permissionRequester.onEnable();
            return true;
        } catch (Throwable throwable) {
            getLogger().error("启动由" + Plugin.getChineseName(plugin) + "提供的权限请求器时出现异常", throwable);

            this.permissionRequester = corePermissionRequester;
            this.plugin = null;
            return false;
        }
    }

    @Override
    public boolean hasPermission(XiaomingUser user, Permission permission) {
        // if a user is console xiaoming user,
        // he must has permission
        if (user instanceof ConsoleXiaomingUser) {
            return true;
        }

        // if user is banned, he hasn't permission
        final Account account = user.getAccount();
        return !account.isBanned() && (account.isAdministrator() || permissionRequester.hasPermission(user, permission));
    }

    @Override
    public boolean hasPermission(Account account, Permission permission) {
        // bot itself must has permission
        if (account.getCode() == xiaomingBot.getCode()) {
            return true;
        }

        return !account.isBanned() && (account.isAdministrator() || permissionRequester.hasPermission(account, permission));
    }

    @Override
    public boolean hasPermission(Account account, GroupInformation groupInformation, Permission permission) {
        // bot itself must has permission
        if (account.getCode() == xiaomingBot.getCode()) {
            return true;
        }

        return !account.isBanned() && permissionRequester.hasPermission(account, groupInformation, permission);
    }

    @Override
    public boolean hasPermission(long authorierCode, long groupCode, Permission permission) {
        final XiaomingBot xiaomingBot = getXiaomingBot();
        final Account account = xiaomingBot.getAccountManager().createAccount(authorierCode);

        return hasPermission(account, xiaomingBot.getGroupInformationManager().addGroupInformation(groupCode), permission);
    }
}
