package cn.chuanwise.xiaoming.permission;

import cn.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 小明权限组管理器
 * @author Chuanwise 
 */
@Data
public class PermissionManagerImpl extends FilePreservableImpl implements PermissionManager {
    Map<String, PermissionGroup> groups = new ConcurrentHashMap<>();
    Map<Long, PermissionUserNode> users = new ConcurrentHashMap<>();
    transient PermissionGroup defaultGroup;

    transient XiaomingBot xiaomingBot;

    public PermissionManagerImpl() {
        this(null);
    }

    // 无论如何，确保存在一个默认组
    public PermissionManagerImpl(XiaomingBot xiaomingBot) {
        this.xiaomingBot = xiaomingBot;

        defaultGroup = new PermissionGroupImpl();
        defaultGroup.setAlias("默认组");
        addGroup(DEFAULT_PERMISSION_GROUP, defaultGroup);
    }

    public void setGroups(Map<String, PermissionGroup> groups) {
        this.groups = groups;
        PermissionGroup defaultGroup = groups.get(DEFAULT_PERMISSION_GROUP);
        if (Objects.nonNull(defaultGroup)) {
            this.defaultGroup = defaultGroup;
        } else {
            defaultGroup = new PermissionGroupImpl();
            defaultGroup.setAlias("默认组");
            addGroup(DEFAULT_PERMISSION_GROUP, defaultGroup);
        }
        // 刷新权限组名
        for (Map.Entry<String, PermissionGroup> entry : groups.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
    }

    @Override
    public void addGroup(String groupName, PermissionGroup group) {
        if (Objects.equals(groupName, DEFAULT_PERMISSION_GROUP)) {
            this.defaultGroup = group;
        }
        group.setName(groupName);
        groups.put(groupName, group);
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
        PermissionUserNode userNode = forUserNode(qq);
        if (Objects.isNull(userNode)) {
            userNode = new PermissionUserNodeImpl();
            userNode.setGroup(PermissionManager.DEFAULT_PERMISSION_GROUP);
            users.put(qq, ((PermissionUserNodeImpl) userNode));
        }
        return userNode;
    }

    @Override
    public boolean isSuper(String superName, String sonName) {
        final PermissionGroup sonGroup = forPermissionGroup(sonName);
        final PermissionGroup superGroup = forPermissionGroup(superName);

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