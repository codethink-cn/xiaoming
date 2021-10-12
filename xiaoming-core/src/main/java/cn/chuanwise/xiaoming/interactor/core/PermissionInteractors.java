package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.interactor.handler.InteractorHandler;
import cn.chuanwise.xiaoming.permission.*;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;
import cn.chuanwise.xiaoming.permission.PermissionGroupImpl;
import cn.chuanwise.xiaoming.util.InteractorUtil;

import java.util.*;

public class PermissionInteractors extends SimpleInteractors {
    PermissionManager permissionManager;

    @Override
    public void onRegister() {
        permissionManager = xiaomingBot.getPermissionManager();
    }

    /** 新增权限组 */
    @Filter(CommandWords.NEW + CommandWords.PERMISSION_GROUP + " {权限组名}")
    @Filter(CommandWords.ADD + CommandWords.PERMISSION_GROUP + " {权限组名}")
    @Permission("permission.group.new")
    public void onNewPermissionGroup(XiaomingUser user,
                                     @FilterParameter("权限组名") String name) {
        PermissionGroup group = permissionManager.forPermissionGroup(name);
        if (Objects.nonNull(group)) {
            user.sendError("{lang.permissionGroupAlreadyExists}");
        } else {
            final PermissionGroup permissionGroup = new PermissionGroupImpl();
            final String superPermissionGroup = PermissionManager.DEFAULT_PERMISSION_GROUP;
            permissionGroup.addSuperGroup(superPermissionGroup);

            permissionManager.addGroup(name, permissionGroup);
            getXiaomingBot().getFileSaver().readyToSave(permissionManager);

            user.sendMessage("{lang.permissionGroupCreated}", permissionGroup, superPermissionGroup);
        }
    }

    /** 权限列表 */
    @Filter(CommandWords.PERMISSION + CommandWords.LIST)
    @Permission("permission.list")
    public void onListPermissions(XiaomingUser user) {
        final List<InteractorHandler> interactors = getXiaomingBot().getInteractorManager().getInteractors();
        final Set<String> permissions = new HashSet<>(interactors.size());
        interactors.forEach(interactor -> permissions.addAll(Arrays.asList(interactor.listPermissions())));
        final List<String> sortedPermissions = Arrays.asList(permissions.toArray(new String[0]));
        Collections.sort(sortedPermissions);
        InteractorUtil.showCollection(user, sortedPermissions, String::toString, 30);
    }

    /** 获得一个插件注册的所有权限节点 */
    @Filter(CommandWords.PERMISSION + CommandWords.LIST + " {插件名}")
    @Permission("permissions.list")
    public void onListPermissions(XiaomingUser user, @FilterParameter("插件名") Plugin plugin) {
        final List<InteractorHandler> interactors = getXiaomingBot().getInteractorManager().getInteractors(plugin);
        final Set<String> permissions = new HashSet<>(interactors.size());
        interactors.forEach(interactor -> permissions.addAll(Arrays.asList(interactor.listPermissions())));
        final List<String> sortedPermissions = Arrays.asList(permissions.toArray(new String[0]));
        Collections.sort(sortedPermissions);
        InteractorUtil.showCollection(user, sortedPermissions, String::toString, 30);
    }

    /** 设置用户权限组 */
    @Filter(CommandWords.LET + " {qq} {权限组}")
    @Permission("permission.user.let")
    public void onSetUserGroup(XiaomingUser user,
                               @FilterParameter("qq") long qq,
                               @FilterParameter("权限组") String name,
                               @FilterParameter("权限组") PermissionGroup permissionGroup) {
        permissionManager.getOrPutUserNode(qq).setGroup(name);
        user.sendMessage("{lang.userPermissionGroupSet}", qq, permissionGroup);
        getXiaomingBot().getFileSaver().readyToSave(permissionManager);
    }

    /** 删除权限组 */
    @Filter(CommandWords.REMOVE + CommandWords.PERMISSION_GROUP + " {权限组}")
    @Permission("permission.group.remove")
    public void onRemovePermissionGroup(XiaomingUser user,
                                        @FilterParameter("权限组") String name,
                                        @FilterParameter("权限组") PermissionGroup permissionGroup) {
        permissionManager.removeGroup(name);
        user.sendMessage("{lang.permissionGroupRemoved}", permissionGroup);
        getXiaomingBot().getFileSaver().readyToSave(permissionManager);
    }

    /** 设置权限组的别名 */
    @Filter(CommandWords.SET + CommandWords.PERMISSION_GROUP + CommandWords.ALIAS + " {权限组} {r:别名}")
    @Permission("permission.group.alias")
    public void onSetGroupAlias(XiaomingUser user,
                                @FilterParameter("权限组") PermissionGroup permissionGroup,
                                @FilterParameter("别名") String alias) {
        permissionGroup.setAlias(alias);
        user.sendMessage("{lang.permissionGroupAliasSet}", permissionGroup);
        getXiaomingBot().getFileSaver().readyToSave(permissionManager);
    }

    /** 增加权限组群权限 */
    @Filter(CommandWords.ADD + CommandWords.PERMISSION_GROUP + " {权限组} " + CommandWords.GROUP + CommandWords.PERMISSION + " {群标签} {r:权限节点}")
    @Permission("permission.group.add")
    public void onAddGroupGroupPermission(XiaomingUser user,
                                          @FilterParameter("权限组") PermissionGroup permissionGroup,
                                          @FilterParameter("权限组") String groupName,
                                          @FilterParameter("群标签") String tag,
                                          @FilterParameter("权限节点") String node) {
        final PermissionAccessible beforeAccessible = permissionManager.permissionGroupAccessible(permissionGroup, tag, node);
        if (beforeAccessible == PermissionAccessible.ACCESSABLE) {
            user.sendWarning("{lang.permissionGroupAlreadyHasThisGroupPermission}", permissionGroup, tag, node);
        } else {
            permissionGroup.getOrPutGroupPermission(tag).add(node);
            getXiaomingBot().getFileSaver().readyToSave(permissionManager);
            user.sendMessage("{lang.permissionGroupGroupPermissionAdded}", permissionGroup, tag, node);
        }
    }

    /** 增加用户群权限 */
    @Filter(CommandWords.ADD + CommandWords.USER + " {qq} " + CommandWords.GROUP + CommandWords.PERMISSION + " {群标签} {r:权限节点}")
    @Permission("permission.group.add")
    public void onAddUserGroupPermission(XiaomingUser user,
                                          @FilterParameter("qq") long qq,
                                          @FilterParameter("群标签") String tag,
                                          @FilterParameter("权限节点") String node) {
        final PermissionUserNode userNode = permissionManager.getOrPutUserNode(qq);
        final PermissionAccessible beforeAccessible = permissionManager.userAccessible(qq, tag, node);
        if (beforeAccessible == PermissionAccessible.ACCESSABLE) {
            user.sendWarning("{lang.userAlreadyHasThisGroupPermission}", qq, tag, node);
        } else {
            userNode.addPermission(node);
            getXiaomingBot().getFileSaver().readyToSave(permissionManager);
            user.sendMessage("{lang.userGroupPermissionAdded}", qq, tag, node);
        }
    }

    /** 删除权限组群权限 */
    @Filter(CommandWords.REMOVE + CommandWords.PERMISSION_GROUP + " {权限组} " + CommandWords.GROUP + CommandWords.PERMISSION + " {群标签} {r:权限节点}")
    @Permission("permission.group.add")
    public void onRemoveGroupGroupPermission(XiaomingUser user,
                                             @FilterParameter("权限组") PermissionGroup permissionGroup,
                                             @FilterParameter("权限组") String groupName,
                                             @FilterParameter("群标签") String tag,
                                             @FilterParameter("权限节点") String node) {
        final PermissionAccessible beforeAccessible = permissionManager.permissionGroupAccessible(permissionGroup, tag, node);
        if (beforeAccessible == PermissionAccessible.ACCESSABLE) {
            final List<String> groupPermission = permissionGroup.getGroupPermission(tag);
            if (Objects.nonNull(groupPermission)) {
                groupPermission.remove(node);
                if (groupPermission.isEmpty()) {
                    permissionGroup.getGroupPermissions().remove(tag);
                }
            }

            if (permissionManager.permissionGroupAccessible(permissionGroup, tag, node) == PermissionAccessible.ACCESSABLE) {
                final List<String> permissions = permissionGroup.getPermissions();
                final List<String> afterInserted = new ArrayList<>(permissions.size() + 1);
                afterInserted.add("-" + node);
                afterInserted.addAll(permissions);
                permissionGroup.setPermissions(afterInserted);
                user.sendMessage("{lang.permissionGroupGroupPermissionRemovedWithDeleteNode}", permissionGroup, tag, node);
            } else {
                user.sendMessage("{lang.permissionGroupGroupPermissionRemoved}", permissionGroup, tag, node);
            }
            getXiaomingBot().getFileSaver().readyToSave(permissionManager);
        } else {
            user.sendWarning("{lang.permissionGroupHadNotPermissionInGroup}", permissionGroup, tag, node);
        }
    }

    /** 删除用户群权限 */
    @Filter(CommandWords.REMOVE + CommandWords.USER + " {qq} " + CommandWords.GROUP + CommandWords.PERMISSION + " {群标签} {r:权限节点}")
    @Permission("permission.group.add")
    public void onRemoveUserGroupPermission(XiaomingUser user,
                                             @FilterParameter("qq") long qq,
                                             @FilterParameter("群标签") String tag,
                                             @FilterParameter("权限节点") String node) {
        final PermissionAccessible beforeAccessible = permissionManager.userAccessible(qq, tag, node);
        final PermissionUserNode userNode = permissionManager.getOrPutUserNode(qq);
        if (beforeAccessible == PermissionAccessible.ACCESSABLE) {
            final List<String> groupPermission = userNode.getGroupPermission(tag);
            groupPermission.remove(node);
            if (groupPermission.isEmpty()) {
                userNode.getGroupPermissions().remove(tag);
            }

            if (permissionManager.userAccessible(qq, tag, node) == PermissionAccessible.ACCESSABLE) {
                final List<String> permissions = userNode.getPermissions();
                final List<String> afterInserted = new ArrayList<>(permissions.size() + 1);
                afterInserted.add("-" + node);
                afterInserted.addAll(permissions);
                userNode.setPermissions(afterInserted);

                user.sendWarning("{lang.userGroupPermissionRemovedWithDeleteNode}", qq, tag, node);
            } else {
                user.sendWarning("{lang.userGroupPermissionRemoved}", qq, tag, node);
            }
            getXiaomingBot().getFileSaver().readyToSave(permissionManager);
        } else {
            user.sendWarning("{lang.userHadNotPermissionInGroup}", tag, node);
        }
    }

    /** 查看某一权限组的信息 */
    @Filter(CommandWords.PERMISSION_GROUP + " {权限组}")
    @Permission("permission.group.look")
    public void onLookPermissionGroup(XiaomingUser user,
                                      @FilterParameter("权限组") String name,
                                      @FilterParameter("权限组") PermissionGroup permissionGroup) {
        user.sendMessage("{lang.permissionGroupDetail}", permissionGroup);
    }

    /** 授权给用户 */
    @Filter(CommandWords.GRANT + " {qq} {r:权限节点}")
    @Filter(CommandWords.SET + CommandWords.USE + CommandWords.PERMISSION_GROUP + " {qq} {r:权限节点}")
    @Permission("permission.user.add.{node}")
    public void onGiveUserPermission(XiaomingUser user,
                                     @FilterParameter("qq") long qq,
                                     @FilterParameter("权限节点") String node) {
        if (permissionManager.userAccessible(qq, node) == PermissionAccessible.ACCESSABLE) {
            user.sendError("{lang.userAlreadyHasThisPermission}", qq, node);
            return;
        }

        final PermissionUserNode userNode = permissionManager.getOrPutUserNode(qq);
        userNode.addPermission(node);
        user.sendMessage("{lang.userPermissionGranted}", qq, node);
        getXiaomingBot().getFileSaver().readyToSave(permissionManager);
    }

    /** 增加组权限 */
    @Filter(CommandWords.NEW + CommandWords.PERMISSION_GROUP + CommandWords.PERMISSION + " {权限组} {r:权限节点}")
    @Filter(CommandWords.GRANT + CommandWords.PERMISSION_GROUP + " {权限组} {r:权限节点}")
    @Filter(CommandWords.PERMISSION_GROUP + " {权限组} " + CommandWords.GRANT + " {r:权限节点}")
    @Permission("permission.group.add")
    public void onAddGroupPermission(XiaomingUser user,
                                     @FilterParameter("权限组") String permissionGroupName,
                                     @FilterParameter("权限组") PermissionGroup permissionGroup,
                                     @FilterParameter("权限节点") String node) {
        if (permissionManager.permissionGroupAccessible(permissionGroup, node) == PermissionAccessible.ACCESSABLE) {
            user.sendMessage("{lang.permissionGroupAlreadyHasPermission}", permissionGroup, node);
        } else {
            permissionGroup.addPermission(node);
            getXiaomingBot().getFileSaver().readyToSave(permissionManager);
            user.sendMessage("{lang.permissionGroupPermissionGranted}", permissionGroupName, node);
        }
    }

    /** 确认组权限 */
    @Filter(CommandWords.PERMISSION_GROUP + " {权限组} " + CommandWords.PERMISSION_CONFIRM + " {r:权限节点}")
    @Permission("permission.group.confirm")
    public void onConfirmGroupPermission(XiaomingUser user,
                                         @FilterParameter("权限组") String name,
                                         @FilterParameter("权限组") PermissionGroup permissionGroup,
                                         @FilterParameter("权限节点") String node) {
        if (permissionManager.permissionGroupAccessible(permissionGroup, node) == PermissionAccessible.ACCESSABLE) {
            user.sendMessage("{lang.permissionGroupHasPermission}", permissionGroup, node);
        } else {
            user.sendMessage("{lang.permissionGroupHasNotPermission}", permissionGroup, node);
        }
    }

    /** 查看所有的权限组 */
    @Filter(CommandWords.PERMISSION_GROUP)
    @Permission("permission.group.list")
    public void onListPermissionGroup(XiaomingUser user) {
        final Map<String, PermissionGroup> groups = permissionManager.getGroups();
        user.sendMessage("{lang.permissionGroupList}", groups.values());
    }

    /** 确认玩家权限 */
    @Filter(CommandWords.USER + " {qq} " + CommandWords.PERMISSION_CONFIRM + " {r:权限节点}")
    @Permission("permission.user.confirm")
    public void onConfirmUserPermission(XiaomingUser user,
                                        @FilterParameter("qq") long qq,
                                        @FilterParameter("权限节点") String node) {
        if (permissionManager.userAccessible(qq, node) == PermissionAccessible.ACCESSABLE) {
            user.sendMessage("{lang.userHasPermission}", qq, node);
        } else {
            user.sendMessage("{lang.userHasNotPermission}", qq, node);
        }
    }

    /** 删除玩家权限 */
    @Filter(CommandWords.REVOKE + " {qq} {r:权限节点}")
    @Permission("permission.user.remove.{args.权限节点}")
    public void onRemoveUserPermission(XiaomingUser user,
                                       @FilterParameter("qq") long qq,
                                       @FilterParameter("权限节点") String node) {
        if (permissionManager.removeUserPermission(qq, node)) {
            user.sendMessage("{lang.userPermissionRemoved}", qq, node);
            getXiaomingBot().getFileSaver().readyToSave(permissionManager);
        } else {
            user.sendMessage("{lang.userHasNotPermission}", qq, node);
        }
    }

    /** 删除组权限 */
    @Filter(CommandWords.PERMISSION_GROUP + " {权限组} " + CommandWords.REMOVE + " {r:权限节点}")
    @Filter(CommandWords.PERMISSION_GROUP + " {权限组} " + CommandWords.REVOKE + " {r:权限节点}")
    @Permission("permission.group.remove")
    public void onRemoveGroupPermission(XiaomingUser user,
                                        @FilterParameter("权限组") String name,
                                        @FilterParameter("权限组") PermissionGroup permissionGroup,
                                        @FilterParameter("权限节点") String node) {
        if (node.startsWith("-")) {
            user.sendError("{lang.illegalPermissionNode}", node);
            return;
        }

        if (permissionManager.permissionGroupAccessible(permissionGroup, node) != PermissionAccessible.ACCESSABLE) {
            user.sendMessage("{lang.permissionGroupHasNotPermission}", permissionGroup, node);
        } else {
            permissionGroup.removePermission(node);
            getXiaomingBot().getFileSaver().readyToSave(permissionManager);

            if (permissionManager.permissionGroupAccessible(permissionGroup, node) == PermissionAccessible.ACCESSABLE) {
                List<String> permissions = new ArrayList<>();
                permissions.add('-' + node);
                permissions.addAll(permissionGroup.getPermissions());
                permissionGroup.setPermissions(permissions);

                user.sendMessage("{lang.permissionGroupPermissionRemovedWithDeleteNode}", permissionGroup, node);
            } else {
                user.sendMessage("{lang.permissionGroupPermissionRemoved}", permissionGroup, node);
            }
        }
    }

    /** 查看用户权限 */
    @Filter(CommandWords.USER + CommandWords.PERMISSION + " {qq}")
    @Permission("permission.user.look")
    public void onLookUserPermission(XiaomingUser user,
                                     @FilterParameter("qq") long qq) {
        PermissionUserNode userNode = permissionManager.forUserNode(qq);
        if (Objects.isNull(userNode)) {
            user.sendMessage("{lang.userBelongToDefaultPermissionGroup}", permissionManager.getDefaultGroup());
        } else {
            user.sendMessage("{lang.userPermissions}", userNode);
        }
    }

    /** 设置继承关系 */
    @Filter(CommandWords.PERMISSION_GROUP + " {父权限组} " + CommandWords.EXTENDS + " {子权限组}")
    @Filter(CommandWords.PERMISSION_GROUP + " {子权限组} " + CommandWords.INHERIT + " {父权限组}")
    @Permission("permission.group.extends.build")
    public void onAddGroupSuper(XiaomingUser user,
                                @FilterParameter("父权限组") String superGroupName,
                                @FilterParameter("子权限组") String sonGroupName) {
        final PermissionGroup superGroup = permissionManager.forPermissionGroup(superGroupName);
        final PermissionGroup sonGroup = permissionManager.forPermissionGroup(sonGroupName);

        if (Objects.isNull(superGroup)) {
            user.sendError("{lang.noSuchPermissionGroup}", superGroupName);
            return;
        }
        if (Objects.isNull(sonGroup)) {
            user.sendError("{lang.noSuchPermissionGroup}", sonGroupName);
            return;
        }

        if (permissionManager.isSuper(superGroupName, sonGroupName)) {
            user.sendError("{lang.canNotAddExtendRelationBecauseExtended}", superGroup, sonGroup);
            return;
        } else if (permissionManager.isSuper(sonGroupName, superGroupName)) {
            user.sendError("{lang.canNotAddExtendRelationBecauseLoop}", superGroup, sonGroup);
        } else {
            sonGroup.getSuperGroups().add(superGroupName);
            getXiaomingBot().getFileSaver().readyToSave(permissionManager);
            user.sendMessage("{lang.permissionGroupExtendRelationAdded}", sonGroup, superGroup);
        }
    }

    /** 取消继承关系 */
    @Filter(CommandWords.PERMISSION_GROUP + " {父权限组} " + CommandWords.CANCEL + CommandWords.EXTENDS + " {子权限组}")
    @Filter(CommandWords.PERMISSION_GROUP + " {子权限组} " + CommandWords.CANCEL + CommandWords.INHERIT + " {父权限组}")
    @Permission("permission.group.extends.cancel")
    public void onRemoveGroupSuper(XiaomingUser user,
                                   @FilterParameter("父权限组") String superGroupName,
                                   @FilterParameter("子权限组") String sonGroupName) {
        final PermissionGroup superGroup = permissionManager.forPermissionGroup(superGroupName);
        final PermissionGroup sonGroup = permissionManager.forPermissionGroup(sonGroupName);

        if (Objects.isNull(superGroup)) {
            user.sendError("{lang.noSuchPermissionGroup}", superGroupName);
            return;
        }
        if (Objects.isNull(sonGroup)) {
            user.sendError("{lang.noSuchPermissionGroup}", sonGroupName);
            return;
        }

        if (permissionManager.isSuper(superGroupName, sonGroupName)) {
            sonGroup.getSuperGroups().remove(superGroupName);
            getXiaomingBot().getFileSaver().readyToSave(permissionManager);
            user.sendMessage("{lang.permissionGroupExtendRelationRemoved}", sonGroup, superGroup);
        } else {
            user.sendError("{lang.permissionGroupHadNotExtendRelation}", superGroup, sonGroup);
        }
    }
}