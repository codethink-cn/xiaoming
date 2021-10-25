package cn.chuanwise.xiaoming.permission;

import cn.chuanwise.util.ArrayUtil;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.group.GroupInformation;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.preservable.SimplePreservable;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.PermissionUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class SimpleCorePermissionRequester
        extends SimplePreservable
        implements CorePermissionRequester {
    List<Permission> userPermissions = new ArrayList<>();

    @Override
    public boolean hasPermission(XiaomingUser user, Permission permission) {
        final Account account = user.getAccount();
        if (user instanceof GroupXiaomingUser) {
            final GroupXiaomingUser groupXiaomingUser = (GroupXiaomingUser) user;
            return hasPermission(account, groupXiaomingUser.getGroupInformation(), permission);
        } else {
            return hasPermission(account, permission);
        }
    }

    @Override
    public boolean hasPermission(Account account, Permission permission) {
        return account.isAdministrator() || userHasPermission(permission);
    }

    @Override
    public boolean hasPermission(Account account, GroupInformation groupInformation, Permission permission) {
        return account.isAdministrator() || userHasPermission(permission);
    }

    protected boolean userHasPermission(Permission permission) {
        // check user permission
        for (Permission userPermission : userPermissions) {
            final Accessible acceptable = userPermission.acceptable(permission);
            if (acceptable != Accessible.UNKNOWN) {
                return acceptable == Accessible.ACCESSIBLE;
            }
        }

        // check plugin permissions
        final Collection<Plugin> plugins = xiaomingBot.getPluginManager().getPlugins().values();
        for (Plugin plugin : plugins) {
            final Permission[] userPermissions = plugin.getHandler().getUserPermissions();
            for (Permission userPermission : userPermissions) {
                final Accessible acceptable = userPermission.acceptable(permission);
                if (acceptable != Accessible.UNKNOWN) {
                    return acceptable == Accessible.ACCESSIBLE;
                }
            }
        }
        return false;
    }
}
