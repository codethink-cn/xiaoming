package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.permission.PermissionAccessible;
import com.chuanwise.xiaoming.api.permission.PermissionGroup;
import com.chuanwise.xiaoming.api.permission.PermissionManager;
import com.chuanwise.xiaoming.api.permission.PermissionUserNode;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.StringUtil;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;
import com.chuanwise.xiaoming.core.permission.PermissionGroupImpl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class PermissionCommandInteractor extends CommandInteractorImpl {
    final PermissionManager permissionManager;

    public PermissionCommandInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        permissionManager = getXiaomingBot().getPermissionManager();
        enableUsageCommand(CommandWords.PERMISSION_GROUP);
    }

    static final String LET = "(委任|指派|委派|任命|let)";
    static final String INHERIT = "(继承|扩展|继承自|extends|inherit)";
    static final String EXTENDS = "(派生|derive)";
    static final String GRANT = "(授权|grant)";
    static final String REVOKE = CommandWords.REMOVE + GRANT;
    static final String PERMISSION_CONFIRM = "(确权|confirm)";

    public String getPermissionGroupName(String name) {
        PermissionGroup permissionGroup = permissionManager.getPermissionGroup(name);
        if (Objects.isNull(permissionGroup) || Objects.isNull(permissionGroup.getAlias())) {
            return name;
        } else {
            return permissionGroup.getAlias() + "（" + name + "）";
        }
    }

    @Override
    public <T> Object onParameter(XiaomingUser user, Class<T> clazz, String parameterName, String currentValue, String defaultValue) {
        Object result = super.onParameter(user, clazz, parameterName, currentValue, defaultValue);
        if (Objects.equals(parameterName, "permissionGroup") && PermissionGroup.class.isAssignableFrom(clazz)) {
            final PermissionGroup permissionGroup = permissionManager.getPermissionGroup(currentValue);
            if (Objects.nonNull(permissionGroup)) {
                result = permissionGroup;
            } else {
                user.sendError("小明找不到权限组：{}", currentValue);
            }
        }
        return result;
    }

    /**
     * 新增权限组
     */
    @Filter(CommandWords.NEW + CommandWords.PERMISSION_GROUP + " {permissionGroup}")
    @RequirePermission("permission.group.new")
    public void onNewPermissionGroup(XiaomingUser user,
                                     @FilterParameter("permissionGroup") String name) {
        PermissionGroup group = permissionManager.getPermissionGroup(name);
        if (Objects.nonNull(group)) {
            user.sendError("权限组{}已经已经存在了", getPermissionGroupName(name));
        } else {
            PermissionGroup permissionGroup = new PermissionGroupImpl();
            permissionGroup.addSuperGroup(PermissionManager.DEFAULT_PERMISSION_GROUP);
            permissionManager.addGroup(name, permissionGroup);
            getXiaomingBot().getFinalizer().readySave(permissionManager);
            user.sendMessage("已增加新的权限组：{}，小明已经将其继承自{}了",
                    name, getPermissionGroupName(PermissionManager.DEFAULT_PERMISSION_GROUP));
        }
    }

    /**
     * 设置用户权限组
     */
    @Filter(LET + " {qq} {permissionGroup}")
    @RequirePermission("permission.user.let")
    public void onSetUserGroup(XiaomingUser user,
                               @FilterParameter("qq") long qq,
                               @FilterParameter("permissionGroup") String name,
                               @FilterParameter("permissionGroup") PermissionGroup permissionGroup) {
        permissionManager.getOrPutUserNode(qq).setGroup(name);
        user.sendMessage("成功设置用户的权限组为：{}", getPermissionGroupName(name));
        getXiaomingBot().getFinalizer().readySave(permissionManager);
    }

    /**
     * 删除权限组
     */
    @Filter(CommandWords.REMOVE + CommandWords.PERMISSION_GROUP + " {permissionGroup}")
    @RequirePermission("permission.group.remove")
    public void onRemovePermissionGroup(XiaomingUser user,
                                        @FilterParameter("permissionGroup") String name,
                                        @FilterParameter("permissionGroup") PermissionGroup permissionGroup) {
        permissionManager.removeGroup(name);
        user.sendMessage("已删除权限组{}", getPermissionGroupName(name));
        getXiaomingBot().getFinalizer().readySave(permissionManager);
    }

    /**
     * 设置权限组的别名
     */
    @Filter(CommandWords.SET + CommandWords.PERMISSION_GROUP + CommandWords.ALIAS + " {permissionGroup} {alias}")
    @RequirePermission("permission.group.alias")
    public void onSetGroupAlias(XiaomingUser user,
                                @FilterParameter("permissionGroup") PermissionGroup permissionGroup,
                                @FilterParameter("alias") String alias) {
        String elderAlias = permissionGroup.getAlias();
        permissionGroup.setAlias(alias);
        getXiaomingBot().getFinalizer().readySave(permissionManager);

        if (Objects.isNull(elderAlias)) {
            user.sendMessage("已为该权限组创建了备注：{}", alias);
        } else {
            user.sendMessage("已将该权限组的备注由 {}改为：{}", elderAlias, alias);
        }
    }

    /**
     * 增加权限组群权限
     */
    @Filter(CommandWords.ADD + CommandWords.PERMISSION_GROUP + " {permissionGroup} " + CommandWords.GROUP + CommandWords.PERMISSION + " {tag} {node}")
    @RequirePermission("permission.group.add")
    public void onAddGroupGroupPermission(XiaomingUser user,
                                          @FilterParameter("permissionGroup") PermissionGroup permissionGroup,
                                          @FilterParameter("permissionGroup") String groupName,
                                          @FilterParameter("tag") String tag,
                                          @FilterParameter("node") String node) {
        final PermissionAccessible beforeAccessible = permissionManager.permissionGroupAccessible(permissionGroup, tag, node);
        if (beforeAccessible == PermissionAccessible.ACCESSABLE) {
            user.sendWarn("{}已经具备带有{}标记的群中的权限{}了", getPermissionGroupName(groupName), tag, node);
        } else {
            permissionGroup.getOrPutGroupPermission(tag).add(node);
            getXiaomingBot().getFinalizer().readySave(permissionManager);
            user.sendMessage("成功为{}增加在有{}标记的群中的权限{}", getPermissionGroupName(groupName), tag, node);
        }
    }

    /**
     * 增加用户群权限
     */
    @Filter(CommandWords.ADD + CommandWords.USER + " {qq} " + CommandWords.GROUP + CommandWords.PERMISSION + " {tag} {node}")
    @RequirePermission("permission.group.add")
    public void onAddUserGroupPermission(XiaomingUser user,
                                          @FilterParameter("qq") long qq,
                                          @FilterParameter("tag") String tag,
                                          @FilterParameter("node") String node) {
        final PermissionUserNode userNode = permissionManager.getOrPutUserNode(qq);
        final PermissionAccessible beforeAccessible = permissionManager.userAccessible(qq, tag, node);
        if (beforeAccessible == PermissionAccessible.ACCESSABLE) {
            user.sendWarn("该用户已经具备带有{}标记的群中的权限{}了", tag, node);
        } else {
            userNode.addPermission(node);
            getXiaomingBot().getFinalizer().readySave(permissionManager);
            user.sendMessage("成功为该用户增加在有{}标记的群中的权限{}", tag, node);
        }
    }

    /**
     * 删除权限组群权限
     */
    @Filter(CommandWords.REMOVE + CommandWords.PERMISSION_GROUP + " {permissionGroup} " + CommandWords.GROUP + CommandWords.PERMISSION + " {tag} {node}")
    @RequirePermission("permission.group.add")
    public void onRemoveGroupGroupPermission(XiaomingUser user,
                                             @FilterParameter("permissionGroup") PermissionGroup permissionGroup,
                                             @FilterParameter("permissionGroup") String groupName,
                                             @FilterParameter("tag") String tag,
                                             @FilterParameter("node") String node) {
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

                user.sendWarn("小明尝试删除了{}在带有{}标记的群中的权限{}，但其父权限组仍具备该权限。小明已经帮当前组增加了权限节点 -{} 以强制删除该权限", getPermissionGroupName(groupName), tag, node, node);
            } else {
                user.sendWarn("成功删除了{}在带有{}标记的群中的权限{}", getPermissionGroupName(groupName), tag, node);
            }
            getXiaomingBot().getFinalizer().readySave(permissionManager);
        } else {
            user.sendWarn("{}还并不具备在带有{}标记的群中的权限{}", getPermissionGroupName(groupName), tag, node);
        }
    }

    /**
     * 删除用户群权限
     */
    @Filter(CommandWords.REMOVE + CommandWords.USER + " {qq} " + CommandWords.GROUP + CommandWords.PERMISSION + " {tag} {node}")
    @RequirePermission("permission.group.add")
    public void onRemoveUserGroupPermission(XiaomingUser user,
                                             @FilterParameter("qq") long qq,
                                             @FilterParameter("tag") String tag,
                                             @FilterParameter("node") String node) {
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

                user.sendWarn("小明尝试删除了该用户在带有{}标记的群中的权限{}，但其父权限组仍具备该权限。小明已经帮该用户增加了权限节点 -{} 以强制删除该权限", tag, node, node);
            } else {
                user.sendWarn("成功删除了该用户在带有{}标记的群中的权限{}",tag, node);
            }
            getXiaomingBot().getFinalizer().readySave(permissionManager);
        } else {
            user.sendWarn("该用户还并不具备在带有{}标记的群中的权限{}", tag, node);
        }
    }

    /**
     * 查看某一权限组的信息
     */
    @Filter(CommandWords.PERMISSION_GROUP + " {permissionGroup}")
    @RequirePermission("permission.group.look")
    public void onLookPermissionGroup(XiaomingUser user,
                                      @FilterParameter("permissionGroup") String name,
                                      @FilterParameter("permissionGroup") PermissionGroup permissionGroup) {
        final StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        printWriter.println("【权限组信息】");
        printWriter.println("权限组名：" + name);
        printWriter.println("备注：" + (Objects.isNull(permissionGroup.getAlias()) ? "（无）" : permissionGroup.getAlias()));
        printWriter.println("父权限组：" + StringUtil.getCollectionSummary(permissionGroup.getSuperGroups(), String::toString, "\n", "（无）", "\n"));
        printWriter.println("特有权限：" + StringUtil.getCollectionSummary(permissionGroup.getPermissions(), String::toString, "\n", "（无）", "\n"));
        printWriter.print("群组权限：" + StringUtil.getCollectionSummary(permissionGroup.getGroupPermissions().entrySet(), entry -> {
            final String tag = entry.getKey();
            return tag + "：" + StringUtil.getCollectionSummary(entry.getValue(), String::toString, "", "（无）", "，");
        }, "\n", "（无）", "\n"));

        user.sendMessage(stringWriter.toString());
    }

    /**
     * 授权给用户
     */
    @Filter(GRANT + " {qq} {node}")
    @Filter(CommandWords.SET + CommandWords.USE + CommandWords.PERMISSION_GROUP + " {qq} {node}")
    public void onGiveUserPermission(XiaomingUser user,
                                     @FilterParameter("qq") long qq,
                                     @FilterParameter("node") String node) {
        final String requiredPermission = "permission.user.add." + node;
        if (!user.requirePermission(requiredPermission)) {
            return;
        }

        final PermissionUserNode userNode = permissionManager.getOrPutUserNode(qq);
        userNode.addPermission(node);
        user.sendMessage("已授予 {} 权限节点：{}", qq, node);
        getXiaomingBot().getFinalizer().readySave(permissionManager);
    }

    /**
     * 增加组权限
     */
    @Filter(CommandWords.NEW + CommandWords.PERMISSION_GROUP + CommandWords.PERMISSION + " {permissionGroup} {node}")
    @Filter(GRANT + CommandWords.PERMISSION_GROUP + " {permissionGroup} {node}")
    @Filter(CommandWords.PERMISSION_GROUP + " {permissionGroup} " + GRANT + " {node}")
    @RequirePermission("permission.group.add")
    public void onAddGroupPermission(XiaomingUser user,
                                     @FilterParameter("permissionGroup") String permissionGroupName,
                                     @FilterParameter("permissionGroup") PermissionGroup permissionGroup,
                                     @FilterParameter("node") String node) {
        if (permissionManager.permissionGroupAccessible(permissionGroup, node) == PermissionAccessible.ACCESSABLE) {
            user.sendMessage("{}已经具有权限：{}了", getPermissionGroupName(permissionGroupName), node);
        } else {
            permissionGroup.addPermission(node);
            getXiaomingBot().getFinalizer().readySave(permissionManager);
            user.sendMessage("成功为{}增加了权限：{}", getPermissionGroupName(permissionGroupName), node);
        }
    }

    /**
     * 确认组权限
     */
    @Filter(CommandWords.PERMISSION_GROUP + " {permissionGroup} " + PERMISSION_CONFIRM + " {node}")
    @RequirePermission("permission.group.confirm")
    public void onConfirmGroupPermission(XiaomingUser user,
                                         @FilterParameter("permissionGroup") String name,
                                         @FilterParameter("permissionGroup") PermissionGroup permissionGroup,
                                         @FilterParameter("node") String node) {
        user.sendMessage("{}" + (permissionManager.permissionGroupAccessible(permissionGroup, node) == PermissionAccessible.ACCESSABLE ? "有" : "没有") +
                "权限：{}", getPermissionGroupName(name), node);
    }

    /**
     * 查看所有的权限组
     */
    @Filter(CommandWords.PERMISSION_GROUP)
    @RequirePermission("permission.group.list")
    public void onListPermissionGroup(XiaomingUser user) {
        final Map<String, PermissionGroup> groups = permissionManager.getGroups();
        user.sendMessage("当前共有 " + groups.size() + " 个权限组：" +
                StringUtil.getCollectionSummary(groups.keySet(), this::getPermissionGroupName, "\n", "（无）", "\n"));
    }

    /**
     * 确认玩家权限
     */
    @Filter(CommandWords.USER + " {qq} " + PERMISSION_CONFIRM + " {node}")
    @RequirePermission("permission.user.confirm")
    public void onConfirmUserPermission(XiaomingUser user,
                                        @FilterParameter("qq") long qq,
                                        @FilterParameter("node") String node) {
        user.sendMessage("该用户" + (permissionManager.userAccessible(qq, node) == PermissionAccessible.ACCESSABLE ? "有" : "没有") + "权限：{}", node);
    }

    /**
     * 删除玩家权限
     * @param user 指令发出者
     * @param qq 目标玩家
     * @param node 要删除的权限
     */
    @Filter(REVOKE + " {qq} {node}")
    public void onRemoveUserPermission(XiaomingUser user,
                                       @FilterParameter("qq") long qq,
                                       @FilterParameter("node") String node) {
        final String requiredPermission = "permission.user.remove." + node;
        if (!user.requirePermission(requiredPermission)) {
            return;
        }

        if (permissionManager.removeUserPermission(qq, node)) {
            user.sendMessage("已移除该用户的权限：{}", node);
            getXiaomingBot().getFinalizer().readySave(permissionManager);
        } else {
            user.sendMessage("该用户并没有权限：{} 哦", node);
        }
    }

    /**
     * 删除组权限
     */
    @Filter(CommandWords.PERMISSION_GROUP + " {permissionGroup} " + CommandWords.REMOVE + " {node}")
    @Filter(CommandWords.PERMISSION_GROUP + " {permissionGroup} " + REVOKE + " {node}")
    @RequirePermission("permission.group.remove")
    public void onRemoveGroupPermission(XiaomingUser user,
                                        @FilterParameter("permissionGroup") String name,
                                        @FilterParameter("permissionGroup") PermissionGroup permissionGroup,
                                        @FilterParameter("node") String node) {
        if (node.startsWith("-")) {
            user.sendError("{} 并不是一个合理的权限节点哦", node);
            return;
        }

        if (permissionManager.permissionGroupAccessible(permissionGroup, node) != PermissionAccessible.ACCESSABLE) {
            user.sendMessage("{}并不具有权限{}哦", getPermissionGroupName(name), node);
        } else {
            permissionGroup.removePermission(node);
            getXiaomingBot().getFinalizer().readySave(permissionManager);
            if (permissionManager.permissionGroupAccessible(permissionGroup, node) == PermissionAccessible.ACCESSABLE) {
                List<String> permissions = new ArrayList<>();
                permissions.add('-' + node);
                permissions.addAll(permissionGroup.getPermissions());
                permissionGroup.setPermissions(permissions);

                user.sendMessage("成功移除了{}的权限：{}，但是其父权限组仍具有该权限。" +
                        "小明已经帮你增加了权限节点：-{}以在当前组中删除此权限。", getPermissionGroupName(name), node, node);
            } else {
                user.sendMessage("成功移除了权限组{}的权限：{}", getPermissionGroupName(name), node);
            }
        }
    }

    /**
     * 查看用户权限
     */
    @Filter(CommandWords.USER + CommandWords.PERMISSION + " {qq}")
    @RequirePermission("permission.user.look")
    public void onLookUserPermission(XiaomingUser user,
                                     @FilterParameter("qq") long qq) {
        PermissionUserNode userNode = permissionManager.getUserNode(qq);
        if (Objects.isNull(userNode)) {
            user.sendMessage("该用户为{}成员", getPermissionGroupName(PermissionManager.DEFAULT_PERMISSION_GROUP));
        } else {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);

            printWriter.println("用户权限信息：");
            printWriter.println("所属组：" + getPermissionGroupName(userNode.getGroup()));
            printWriter.println("特有权限：" + StringUtil.getCollectionSummary(userNode.getPermissions(), String::toString, "\n", "（无）", "\n"));
            printWriter.print("群组权限：" + StringUtil.getCollectionSummary(userNode.getGroupPermissions().entrySet(), entry -> {
                final String tag = entry.getKey();
                return tag + "：" + StringUtil.getCollectionSummary(entry.getValue(), String::toString, "", "（无）", "，");
            }, "\n", "（无）", "\n"));

            user.sendMessage(stringWriter.toString());
        }
    }

    /**
     * 设置继承关系
     */
    @Filter(CommandWords.PERMISSION_GROUP + " {super} " + EXTENDS + " {son}")
    @Filter(CommandWords.PERMISSION_GROUP + " {son} " + INHERIT + " {super}")
    @RequirePermission("permission.group.extends.link")
    public void onAddGroupSuper(XiaomingUser user,
                                @FilterParameter("super") String superGroupName,
                                @FilterParameter("son") String sonGroupName) {
        final PermissionGroup superGroup = permissionManager.getPermissionGroup(superGroupName);
        final PermissionGroup sonGroup = permissionManager.getPermissionGroup(sonGroupName);

        if (Objects.isNull(superGroup)) {
            user.sendError("找不到父权限组{}", superGroupName);
            return;
        }
        if (Objects.isNull(sonGroup)) {
            user.sendError("找不到子权限组{}", sonGroupName);
            return;
        }

        if (permissionManager.isSuper(superGroupName, sonGroupName)) {
            user.sendError("{}已经是{}的父权限组了，无须重复继承", getPermissionGroupName(sonGroupName), getPermissionGroupName(superGroupName));
            return;
        } else if (permissionManager.isSuper(sonGroupName, superGroupName)) {
            user.sendError("{}已经是{}的父权限组了，无法相互继承", getPermissionGroupName(sonGroupName), getPermissionGroupName(superGroupName));
        } else {
            sonGroup.getSuperGroups().add(superGroupName);
            getXiaomingBot().getFinalizer().readySave(permissionManager);
            user.sendMessage("成功令{}继承了{}的所有权限", getPermissionGroupName(sonGroupName), getPermissionGroupName(superGroupName));
        }
    }

    /**
     * 取消继承关系
     */
    @Filter(CommandWords.PERMISSION_GROUP + " {super} " + CommandWords.CANCEL + EXTENDS + " {son}")
    @Filter(CommandWords.PERMISSION_GROUP + " {son} " + CommandWords.CANCEL + INHERIT + " {super}")
    @RequirePermission("permission.group.extends.cancel")
    public void onRemoveGroupSuper(XiaomingUser user,
                                   @FilterParameter("super") String superGroupName,
                                   @FilterParameter("son") String sonGroupName) {
        final PermissionGroup superGroup = permissionManager.getPermissionGroup(superGroupName);
        final PermissionGroup sonGroup = permissionManager.getPermissionGroup(sonGroupName);

        if (Objects.isNull(superGroup)) {
            user.sendError("找不到父权限组{}", superGroupName);
            return;
        }
        if (Objects.isNull(sonGroup)) {
            user.sendError("找不到子权限组{}", sonGroupName);
            return;
        }

        if (permissionManager.isSuper(superGroupName, sonGroupName)) {
            sonGroup.getSuperGroups().remove(superGroupName);
            getXiaomingBot().getFinalizer().readySave(permissionManager);
            user.sendMessage("成功取消了{}派生{}的联系", getPermissionGroupName(superGroupName), getPermissionGroupName(sonGroupName));
        } else {
            user.sendError("{}并不是{}的父权限组哦", getPermissionGroupName(superGroupName), getPermissionGroupName(sonGroupName));
        }
    }
}