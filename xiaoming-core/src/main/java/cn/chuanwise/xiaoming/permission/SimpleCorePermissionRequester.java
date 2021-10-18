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
    List<String> userPermissions = new ArrayList<>();

    @Override
    public boolean hasPermission(XiaomingUser user, String permission) {
        final Account account = user.getAccount();
        if (user instanceof GroupXiaomingUser) {
            final GroupXiaomingUser groupXiaomingUser = (GroupXiaomingUser) user;
            return hasPermission(account, groupXiaomingUser.getGroupInformation(), permission);
        } else {
            return hasPermission(account, permission);
        }
    }

    @Override
    public boolean hasPermission(Account account, String permission) {
        return account.isAdministrator() || userHasPermission(permission);
    }

    @Override
    public boolean hasPermission(Account account, GroupInformation groupInformation, String permission) {
        return account.isAdministrator() || userHasPermission(permission);
    }

    protected boolean userHasPermission(String permission) {
        // check user permission
        if (CollectionUtil.findFirst(userPermissions, owned -> PermissionUtil.isAccessible(owned, permission))
                .isPresent()) {
            return true;
        }

        // check plugin permissions
        final Collection<Plugin> plugins = xiaomingBot.getPluginManager().getPlugins().values();
        for (Plugin plugin : plugins) {
            final String[] userPermissions = plugin.getHandler().getUserPermissions();
            if (ArrayUtil.findFirst(userPermissions, owned -> PermissionUtil.isAccessible(owned, permission))
                    .isPresent()) {
                return true;
            }
        }
        return false;
    }
}
