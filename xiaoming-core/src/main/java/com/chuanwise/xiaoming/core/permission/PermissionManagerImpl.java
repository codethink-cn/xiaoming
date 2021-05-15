package com.chuanwise.xiaoming.core.permission;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.permission.PermissionGroup;
import com.chuanwise.xiaoming.api.permission.PermissionManager;
import com.chuanwise.xiaoming.api.permission.PermissionUserNode;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.PermissionUtil;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 小明权限组管理器
 * @author Chuanwise 
 */
@Data
@NoArgsConstructor
public class PermissionManagerImpl extends JsonFilePreservable implements PermissionManager {
    Map<String, PermissionGroupImpl> groups = new ConcurrentHashMap<>();
    Map<Long, PermissionUserNodeImpl> users = new ConcurrentHashMap<>();
    transient PermissionGroup defaultGroup;

    transient XiaomingBot xiaomingBot;

    public PermissionManagerImpl(XiaomingBot xiaomingBot) {
        this.xiaomingBot = xiaomingBot;
    }

    @Override
    public Map<Long, PermissionUserNode> getUsers() {
        return ((Map) users);
    }

    @Override
    public Map<String, PermissionGroup> getGroups() {
        return (Map) groups;
    }

    public void setGroups(Map<String, PermissionGroupImpl> groups) {
        this.groups = groups;
        PermissionGroup defaultGroup = groups.get(DEFAULT_PERMISSION_GROUP_NAME);
        if (Objects.nonNull(defaultGroup)) {
            this.defaultGroup = defaultGroup;
        } else {
            defaultGroup = new PermissionGroupImpl();
            defaultGroup.setAlias("默认组");
            this.defaultGroup = defaultGroup;
            addGroup(DEFAULT_PERMISSION_GROUP_NAME, defaultGroup);
        }
    }

    @Override
    public boolean userHasPermission(long qq, String node) {
        final PermissionUserNode userNode = getUserNode(qq);
        if (Objects.isNull(userNode)) {
            return groupHasPermission(defaultGroup, node);
        } else {
            try {
                final List<String> permissions = userNode.getPermissions();
                // 先检查私有权限
                if (Objects.nonNull(permissions)) {
                    for (String per : permissions) {
                        final int accessable = PermissionUtil.accessable(per, node);
                        if (accessable == 0) {
                            continue;
                        } else {
                            return accessable > 0;
                        }
                    }
                }
                return Objects.nonNull(userNode.getGroup()) && getXiaomingBot().getPermissionManager().groupHasPermission(userNode.getGroup(), node);
            } catch (Exception exception) {
                exception.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public boolean groupHasPermission(String groupName, final String node) {
        final PermissionGroup group = getGroup(groupName);
        return Objects.nonNull(group) && groupHasPermission(group, node);
    }

    @Override
    public PermissionGroup getGroup(String groupName) {
        return groups.get(groupName);
    }

    @Override
    public boolean groupHasPermission(PermissionGroup group,
                                      final String node) {
        for (String n : group.getPermissions()) {
            final int accessable = PermissionUtil.accessable(n, node);
            if (accessable == 0) {
                continue;
            } else {
                return accessable > 0;
            }
        }
        for (String superGroupName : group.getSuperGroups()) {
            PermissionGroup superGroup = getGroup(superGroupName);
            if (Objects.isNull(superGroup)) {
                return false;
            } else if (groupHasPermission(superGroup, node)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addGroup(String groupName, PermissionGroup group) {
        if (Objects.equals(groupName, DEFAULT_PERMISSION_GROUP_NAME)) {
            this.defaultGroup = group;
        }
        groups.put(groupName, ((PermissionGroupImpl) group));
    }

    @Override
    public void removeGroup(String groupName) {
        groups.remove(groupName);
    }

    @Override
    public boolean removeUserPermission(long qq, String node) {
        if (userHasPermission(qq, node)) {
            final PermissionUserNode userNode = getOrPutUserNode(qq);

            userNode.getPermissions().remove(node);
            if (userHasPermission(qq, node)) {
                List<String> permissions = new ArrayList<>();
                permissions.add('-' + node);
                permissions.addAll(userNode.getPermissions());
                userNode.setPermissions(permissions);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public PermissionUserNode getUserNode(long qq) {
        return users.get(qq);
    }

    @Override
    public PermissionUserNode getOrPutUserNode(long qq) {
        PermissionUserNode userNode = getUserNode(qq);
        if (Objects.isNull(userNode)) {
            userNode = new PermissionUserNodeImpl();
            userNode.setGroup(PermissionManager.DEFAULT_PERMISSION_GROUP_NAME);
            users.put(qq, ((PermissionUserNodeImpl) userNode));
        }
        return userNode;
    }

    @Override
    public boolean isSuper(String superName, String sonName) {
        final PermissionGroup sonGroup = getGroup(sonName);
        final PermissionGroup superGroup = getGroup(superName);

        if (Objects.isNull(sonGroup) || Objects.isNull(superGroup)) {
            return false;
        } else {
            for (String groupName : sonGroup.getSuperGroups()) {
                if (groupName.equals(superName) || isSuper(superName, groupName)) {
                    return true;
                }
            }
            return false;
        }
    }
}