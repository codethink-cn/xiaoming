package com.chuanwise.xiaoming.api.permission;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface PermissionManager extends XiaomingObject, Preservable<File> {
    String DEFAULT_PERMISSION_GROUP_NAME = "default";

    boolean userHasPermission(long qq,
                              String node);

    boolean userHasPermissions(long qq,
                               @NotNull String[] nodes);

    boolean groupHasPermission(String groupName, String node);

    PermissionGroup getGroup(String groupName);

    boolean groupHasPermission(PermissionGroup group,
                               String node);

    void addGroup(String groupName, PermissionGroup group);

    void removeGroup(String groupName);

    boolean removeUserPermission(long qq, String node);

    PermissionUserNode getUserNode(long qq);

    PermissionUserNode getOrPutUserNode(long qq);

    boolean isSuper(String superName, String sonName);

    java.util.Map<String, PermissionGroup> getGroups();

    java.util.Map<Long, PermissionUserNode> getUsers();

    PermissionGroup getDefaultGroup();
}
