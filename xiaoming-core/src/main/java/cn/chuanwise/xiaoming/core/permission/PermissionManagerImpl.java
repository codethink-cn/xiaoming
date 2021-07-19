package cn.chuanwise.xiaoming.core.permission;

import cn.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.permission.PermissionAccessible;
import cn.chuanwise.xiaoming.api.permission.PermissionGroup;
import cn.chuanwise.xiaoming.api.permission.PermissionManager;
import cn.chuanwise.xiaoming.api.permission.PermissionUserNode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 小明权限组管理器
 * @author Chuanwise 
 */
@Data
@NoArgsConstructor
public class PermissionManagerImpl extends FilePreservableImpl implements PermissionManager {
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
        PermissionGroup defaultGroup = groups.get(DEFAULT_PERMISSION_GROUP);
        if (Objects.nonNull(defaultGroup)) {
            this.defaultGroup = defaultGroup;
        } else {
            defaultGroup = new PermissionGroupImpl();
            defaultGroup.setAlias("默认组");
            this.defaultGroup = defaultGroup;
            addGroup(DEFAULT_PERMISSION_GROUP, defaultGroup);
        }
    }

    @Override
    public void addGroup(String groupName, PermissionGroup group) {
        if (Objects.equals(groupName, DEFAULT_PERMISSION_GROUP)) {
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
        if (userAccessible(qq, node) == PermissionAccessible.ACCESSABLE) {
            final PermissionUserNode userNode = getOrPutUserNode(qq);

            userNode.getPermissions().remove(node);
            if (userAccessible(qq, node) == PermissionAccessible.ACCESSABLE) {
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
    public PermissionUserNode getOrPutUserNode(long qq) {
        PermissionUserNode userNode = getUserNode(qq);
        if (Objects.isNull(userNode)) {
            userNode = new PermissionUserNodeImpl();
            userNode.setGroup(PermissionManager.DEFAULT_PERMISSION_GROUP);
            users.put(qq, ((PermissionUserNodeImpl) userNode));
        }
        return userNode;
    }

    @Override
    public boolean isSuper(String superName, String sonName) {
        final PermissionGroup sonGroup = getPermissionGroup(sonName);
        final PermissionGroup superGroup = getPermissionGroup(superName);

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