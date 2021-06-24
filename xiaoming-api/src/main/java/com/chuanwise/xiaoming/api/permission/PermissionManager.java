package com.chuanwise.xiaoming.api.permission;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.PermissionUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 权限管理器
 * 小明的所有权限通过字符串表示。形如 [\S]+(\.[\S])*。例如 stop, lexicons.global.add.*
 * 权限被划分为三个区域：特有权限、群权限和私有权限
 * 不论是权限组用户还是权限组，都有上述三个分量。
 *
 * 每一个群都带有 [unrecorded|recorded]、[群号] 这俩 tag。只有群号 tag 是严格检查，其他则是取最大值检查。
 * @author Chuanwise
 */
public interface PermissionManager extends XiaomingObject, Preservable<File> {
    String DEFAULT_PERMISSION_GROUP = "default";

    default PermissionAccessible userAccessible(XiaomingUser user, String require) {
        if (user instanceof GroupXiaomingUser) {
            return userAccessible(user.getCode(), ((GroupXiaomingUser) user).getGroupCode(), require);
        } else {
            return userAccessible(user.getCode(), require);
        }
    }

    default PermissionAccessible userAccessible(long qq, long group, String require) {
        final PermissionUserNode userNode = getUserNode(qq);
        if (Objects.isNull(userNode)) {
            return groupAccessible(group, getDefaultGroup(), require);
        } else {
            // 检查特有权限
            final List<String> specialPermissions = userNode.getPermissions();
            final PermissionAccessible specialAccessible = PermissionUtils.accessible(specialPermissions, require);
            if (specialAccessible != PermissionAccessible.UNKNOWN) {
                return specialAccessible;
            }

            final String groupName = userNode.getGroup();
            final PermissionGroup permissionGroup = getPermissionGroup(groupName);
            return groupAccessible(group, Objects.nonNull(permissionGroup) ? permissionGroup : getDefaultGroup(), require);
        }
    }

    default PermissionAccessible userAccessible(long qq, String tag, String require) {
        final PermissionUserNode userNode = getUserNode(qq);
        if (Objects.isNull(userNode)) {
            return permissionGroupAccessible(getDefaultGroup(), tag, require);
        } else {
            // 检查特有权限
            final List<String> specialPermissions = userNode.getPermissions();
            final PermissionAccessible specialAccessible = PermissionUtils.accessible(specialPermissions, require);
            if (specialAccessible != PermissionAccessible.UNKNOWN) {
                return specialAccessible;
            }

            final String groupName = userNode.getGroup();
            if (Objects.nonNull(groupName)) {
                final PermissionGroup permissionGroup = getPermissionGroup(groupName);
                if (Objects.nonNull(permissionGroup)) {
                    return permissionGroupAccessible(permissionGroup, tag, require);
                }
            }
            return PermissionAccessible.UNKNOWN;
        }
    }

    default PermissionAccessible userAccessible(long qq, String require) {
        final PermissionUserNode userNode = getUserNode(qq);
        if (Objects.isNull(userNode)) {
            return permissionGroupAccessible(getDefaultGroup(), require);
        } else {
            // 检查特有权限
            final List<String> specialPermissions = userNode.getPermissions();
            final PermissionAccessible specialAccessible = PermissionUtils.accessible(specialPermissions, require);
            if (specialAccessible != PermissionAccessible.UNKNOWN) {
                return specialAccessible;
            }

            final String groupName = userNode.getGroup();
            if (Objects.nonNull(groupName)) {
                final PermissionGroup permissionGroup = getPermissionGroup(groupName);
                if (Objects.nonNull(permissionGroup)) {
                    return permissionGroupAccessible(permissionGroup, require);
                }
            }
            return PermissionAccessible.UNKNOWN;
        }
    }

    default PermissionGroup getPermissionGroup(String groupName) {
        return getGroups().get(groupName);
    }

    default PermissionAccessible permissionGroupAccessible(PermissionGroup permissionGroup, String require) {
        // 先在当前权限组中查找特有权限
        final List<String> specialPermissions = permissionGroup.getPermissions();
        final PermissionAccessible specialAccessible = Objects.nonNull(specialPermissions) ? PermissionUtils.accessible(specialPermissions, require) : PermissionAccessible.UNKNOWN;
        if (specialAccessible != PermissionAccessible.UNKNOWN) {
            return specialAccessible;
        }

        // 找不到就去其父权限组中
        final List<String> superGroups = permissionGroup.getSuperGroups();
        if (specialAccessible == PermissionAccessible.UNKNOWN && !superGroups.isEmpty()) {
            for (String superGroupName : superGroups) {
                // 先找到该组
                final PermissionGroup superGroup = getPermissionGroup(superGroupName);

                // 查询其权限状况
                final PermissionAccessible superStatus = Objects.nonNull(superGroup) ? permissionGroupAccessible(superGroup, require) : PermissionAccessible.UNKNOWN;

                if (superStatus != PermissionAccessible.UNKNOWN) {
                    return superStatus;
                }
            }
        }
        return PermissionAccessible.UNKNOWN;
    }

    default PermissionAccessible permissionGroupAccessible(PermissionGroup permissionGroup, String tag, String require) {
        final PermissionAccessible specialAccessible = permissionGroupAccessible(permissionGroup, require);
        if (specialAccessible != PermissionAccessible.UNKNOWN) {
            return specialAccessible;
        }

        // 先在当前权限组中查找特有权限
        final List<String> specialPermissions = permissionGroup.getGroupPermission(tag);
        final PermissionAccessible groupAccessible = Objects.nonNull(specialPermissions) ? PermissionUtils.accessible(specialPermissions, require) : PermissionAccessible.UNKNOWN;
        if (groupAccessible != PermissionAccessible.UNKNOWN) {
            return groupAccessible;
        }

        // 找不到就去其父权限组中
        final List<String> superGroups = permissionGroup.getSuperGroups();
        for (String superGroupName : superGroups) {
            // 先找到该组
            final PermissionGroup superGroup = getPermissionGroup(superGroupName);

            // 查询其权限状况
            final PermissionAccessible superStatus = Objects.nonNull(superGroup) ? permissionGroupAccessible(superGroup, tag, require) : PermissionAccessible.UNKNOWN;

            if (superStatus != PermissionAccessible.UNKNOWN) {
                return superStatus;
            }
        }
        return PermissionAccessible.UNKNOWN;
    }

    default PermissionAccessible groupAccessible(ResponseGroup responseGroup, PermissionGroup permissionGroup, String require) {
        // 先检测是否有专属 tag
        final String codeTag = String.valueOf(responseGroup.getCode());
        final PermissionAccessible codeTagAccessible = permissionGroupAccessible(permissionGroup, codeTag, require);
        if (codeTagAccessible != PermissionAccessible.UNKNOWN) {
            return codeTagAccessible;
        }

        // 再查查有无 recorded tag
        final PermissionAccessible recordedTagAccessible = permissionGroupAccessible(permissionGroup, codeTag, require);
        if (recordedTagAccessible != PermissionAccessible.UNKNOWN) {
            return recordedTagAccessible;
        }

        // 再查查真正的 tag
        final Set<String> tags = responseGroup.getTags();
        for (String tag : tags) {
            final PermissionAccessible tagAccessible = permissionGroupAccessible(permissionGroup, tag, require);
            if (tagAccessible != PermissionAccessible.UNKNOWN) {
                return tagAccessible;
            }
        }
        return PermissionAccessible.UNKNOWN;
    }

    default PermissionAccessible groupAccessible(long group, PermissionGroup permissionGroup, String require) {
        final PermissionAccessible accessible = permissionGroupAccessible(permissionGroup, require);
        if (accessible != PermissionAccessible.UNKNOWN) {
            return accessible;
        }

        final ResponseGroup responseGroup = getXiaomingBot().getResponseGroupManager().forCode(group);
        if (Objects.isNull(responseGroup)) {
            // 检查 code tag
            final String codeTag = group + "";
            final PermissionAccessible codeTagAccessible = permissionGroupAccessible(permissionGroup, codeTag, require);
            if (codeTagAccessible != PermissionAccessible.UNKNOWN) {
                return codeTagAccessible;
            }

            // 检查全局 tag
            final String unrecordedTag = "unrecorded";
            final PermissionAccessible unrecordedAccessible = permissionGroupAccessible(permissionGroup, unrecordedTag, require);
            return unrecordedAccessible;
        } else {
            return groupAccessible(responseGroup, permissionGroup, require);
        }
    }

    void addGroup(String groupName, PermissionGroup group);

    void removeGroup(String groupName);

    boolean removeUserPermission(long qq, String node);

    default PermissionUserNode getUserNode(long qq) {
        return getUsers().get(qq);
    }

    PermissionUserNode getOrPutUserNode(long qq);

    boolean isSuper(String superName, String sonName);

    Map<String, PermissionGroup> getGroups();

    Map<Long, PermissionUserNode> getUsers();

    PermissionGroup getDefaultGroup();
}
