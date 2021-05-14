package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.permission.PermissionGroup;
import com.chuanwise.xiaoming.api.permission.PermissionManager;
import com.chuanwise.xiaoming.api.permission.PermissionUserNode;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;
import com.chuanwise.xiaoming.core.permission.PermissionGroupImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PermissionCommandInteractor extends CommandInteractorImpl {
    final PermissionManager permissionManager;

    public PermissionCommandInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        permissionManager = getXiaomingBot().getPermissionManager();
        enableUsageCommand(CommandWords.PERMISSION_GROUP_REGEX);
    }

    static final String LET = "(委任|指派|委派|任命|let)";
    static final String INHERIT = "(继承|扩展|继承自|extends|inherit)";
    static final String EXTENDS = "(派生|derive)";
    static final String GRANT = "(授权|grant)";
    static final String REVOKE = CommandWords.REMOVE_REGEX + GRANT;
    static final String PERMISSION_CONFIRM = CommandWords.PERMISSION_GROUP_REGEX + CommandWords.CONFIRM_REGEX;

    public String getPermissionGroupName(String name) {
        PermissionGroup permissionGroup = permissionManager.getGroup(name);
        if (Objects.isNull(permissionGroup) || Objects.isNull(permissionGroup.getAlias())) {
            return name;
        } else {
            return permissionGroup.getAlias() + "（" + name + "）";
        }
    }

    /**
     * 新增权限组
     */
    @Filter(CommandWords.NEW_REGEX + CommandWords.PERMISSION_GROUP_REGEX + " {name}")
    @RequirePermission("permission.group.new")
    public void onNewPermissionGroup(XiaomingUser user,
                                     @FilterParameter("name") String name) {
        PermissionGroup group = permissionManager.getGroup(name);
        if (Objects.nonNull(group)) {
            user.sendMessage("权限组 {} 已经存在了（；´д｀）ゞ", getPermissionGroupName(name));
        } else {
            PermissionGroup permissionGroup = new PermissionGroupImpl();
            permissionGroup.addSuperGroup(PermissionManager.DEFAULT_PERMISSION_GROUP_NAME);
            permissionManager.addGroup(name, permissionGroup);
            getXiaomingBot().getRegularPreserveManager().readySave(permissionManager);
            user.sendMessage("已增加新的权限组：{}，小明已经将其继承自 {} 了",
                    name, getPermissionGroupName(PermissionManager.DEFAULT_PERMISSION_GROUP_NAME));
        }
    }

    /**
     * 设置用户权限组
     */
    @Filter(LET + " {qq} {group}")
    @RequirePermission("permission.user.set")
    public void onSetUserGroup(XiaomingUser user,
                               @FilterParameter("qq") long qq,
                               @FilterParameter("group") String group) {
        PermissionGroup permissionGroup = permissionManager.getGroup(group);
        if (Objects.isNull(group)) {
            user.sendMessage("找不到权限组 {}（；´д｀）ゞ", group);
        } else {
            final Account account = getXiaomingBot().getAccountManager().getOrPutAccount(qq);
            permissionManager.getOrPutUserNode(qq).setGroup(group   );
            user.sendMessage("成功设置用户的权限组为：{}", getPermissionGroupName(group));
        }
    }

    /**
     * 删除权限组
     */
    @Filter(CommandWords.REMOVE_REGEX + CommandWords.PERMISSION_GROUP_REGEX + " {name}")
    @RequirePermission("permission.group.remove")
    public void onRemovePermissionGroup(XiaomingUser user,
                                        @FilterParameter("name") String name) {
        PermissionGroup group = permissionManager.getGroup(name);
        if (Objects.nonNull(group)) {
            user.sendMessage("已删除权限组 {}", getPermissionGroupName(name));
            permissionManager.removeGroup(name);
            getXiaomingBot().getRegularPreserveManager().readySave(permissionManager);
        } else {
            user.sendMessage("小明找不到权限组 {} (ノへ￣、)", getPermissionGroupName(name));
        }
    }

    /**
     * 设置权限组的别名
     */
    @Filter(CommandWords.PERMISSION_GROUP_REGEX + " {name} " + CommandWords.ALIAS_REGEX + " {alias}")
    @RequirePermission("permission.group.alias")
    public void onSetGroupAlias(XiaomingUser user,
                                @FilterParameter("name") String name,
                                @FilterParameter("alias") String alias) {
        PermissionGroup group = permissionManager.getGroup(name);
        if (Objects.nonNull(group)) {
            String elderAlias = group.getAlias();
            group.setAlias(alias);
            getXiaomingBot().getRegularPreserveManager().readySave(permissionManager);
            if (Objects.isNull(elderAlias)) {
                user.sendMessage("已为权限组{}创建了备注：{}", name, alias);
            } else {
                user.sendMessage("已将权限组{}的备注由 {}改为：{}", name, elderAlias, alias);
            }
        } else {
            user.sendError("小明找不到权限组{}", name);
        }
    }

    /**
     * 查看某一权限组的信息
     */
    @Filter(CommandWords.PERMISSION_GROUP_REGEX + " {name}")
    @RequirePermission("permission.group.look")
    public void onLookPermissionGroup(XiaomingUser user,
                                      @FilterParameter("name") String name) {
        PermissionGroup group = permissionManager.getGroup(name);
        if (Objects.nonNull(group)) {
            StringBuilder builder = new StringBuilder("【权限组信息】");
            builder.append("\n").append("权限组名：").append(name)
                    .append("\n").append("备注：").append(Objects.isNull(group.getAlias()) ? "（无）" : group.getAlias())
                    .append("\n").append("父权限组：");
            if (group.getSuperGroups().isEmpty()) {
                builder.append("（无）");
            } else {
                for (String s : group.getSuperGroups()) {
                    builder.append("\n").append(getPermissionGroupName(s));
                }
            }
            builder.append("\n").append("权限节点：");
            if (group.getPermissions().isEmpty()) {
                builder.append("（无）");
            } else {
                for (String node : group.getPermissions()) {
                    builder.append("\n").append(node);
                }
            }
            user.sendMessage(builder.toString());
        } else {
            user.sendError("小明找不到权限组 {}", name);
        }
    }

    /**
     * 授权给用户
     */
    @Filter(GRANT + " {qq} {node}")
    public void onGiveUserPermission(XiaomingUser user,
                                     @FilterParameter("qq") long qq,
                                     @FilterParameter("node") String node) {
        final String requiredPermission = "permission.user.add." + node;
        if (!user.hasPermission(requiredPermission)) {
            user.sendError("小明不能帮你做这件事哦，因为你缺少权限：{}", requiredPermission);
            return;
        }
        final Account account = getXiaomingBot().getAccountManager().getOrPutAccount(qq);
        final PermissionUserNode userNode = permissionManager.getOrPutUserNode(qq);
        userNode.addPermission(node);
        user.sendMessage("已授予 {} 权限节点：{}", qq, node);
    }

    /**
     * 增加组权限
     */
    @Filter(CommandWords.PERMISSION_GROUP_REGEX + " {name} " + CommandWords.NEW_REGEX + " {node}")
    @Filter(CommandWords.PERMISSION_GROUP_REGEX + " {name} " + GRANT + " {node}")
    @RequirePermission("permission.group.add")
    public void onAddGroupPermission(XiaomingUser user,
                                     @FilterParameter("name") String name,
                                     @FilterParameter("node") String node) {
        PermissionGroup group = permissionManager.getGroup(name);
        if (Objects.nonNull(group)) {
            if (permissionManager.groupHasPermission(group, node)) {
                user.sendMessage("权限组 {} 已经具有权限：{} 了", getPermissionGroupName(name), node);
            } else {
                group.addPermission(node);
                getXiaomingBot().getRegularPreserveManager().readySave(permissionManager);
                user.sendMessage("成功为权限组 {} 增加了权限：{}", getPermissionGroupName(name), node);
            }
        } else {
            user.sendMessage("找不到权限组：{}", name);
        }
    }

    /**
     * 确认组权限
     */
    @Filter(CommandWords.PERMISSION_GROUP_REGEX + " {name} " + PERMISSION_CONFIRM + " {node}")
    @RequirePermission("permission.group.confirm")
    public void onConfirmGroupPermission(XiaomingUser user,
                                         @FilterParameter("name") String name,
                                         @FilterParameter("node") String node) {
        PermissionManager system = permissionManager;
        PermissionGroup group = system.getGroup(name);
        if (Objects.nonNull(group)) {
            if (system.groupHasPermission(group, node)) {
                user.sendMessage("权限组 {} 拥有权限：{}", getPermissionGroupName(name), node);
            } else {
                user.sendMessage("权限组 {} 没有权限：{}", getPermissionGroupName(name), node);
            }
        } else {
            user.sendMessage("找不到权限组：{}", name);
        }
    }

    /**
     * 查看所有的权限组
     */
    @Filter(CommandWords.PERMISSION_GROUP_REGEX)
    @RequirePermission("permission.group.list")
    public void onListPermissionGroup(XiaomingUser user) {
        final Map<String, PermissionGroup> groups = permissionManager.getGroups();
        StringBuilder builder = new StringBuilder("当前共有 " + groups.size() + " 个权限组：");
        for (String groupName : groups.keySet()) {
            builder.append("\n").append(getPermissionGroupName(groupName));
        }
        user.sendMessage(builder.toString());
    }

    /**
     * 确认玩家权限
     */
    @Filter(CommandWords.USER_REGEX + " {qq} " + PERMISSION_CONFIRM + " {node}")
    @RequirePermission("permission.user.confirm")
    public void onConfirmUserPermission(XiaomingUser user,
                                        @FilterParameter("qq") long qq,
                                        @FilterParameter("node") String node) {
        if (permissionManager.userHasPermission(qq, node)) {
            user.sendMessage("用户 {} 拥有权限：{}", qq, node);
        } else {
            user.sendMessage("用户 {} 没有权限：{}", qq, node);
        }
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
        if (!user.hasPermission(requiredPermission)) {
            user.sendError("小明不能帮你做这件事哦，因为你缺少权限：{}", requiredPermission);
            return;
        }
        if (permissionManager.removeUserPermission(qq, node)) {
            user.sendMessage("已移除用户 {} 的权限：{}", qq, node);
        } else {
            user.sendMessage("{} 并没有权限：{} 哦", qq, node);
        }
        getXiaomingBot().getRegularPreserveManager().readySave(permissionManager);
    }

    /**
     * 删除组权限
     */
    @Filter(CommandWords.PERMISSION_GROUP_REGEX + " {name} " + CommandWords.REMOVE_REGEX + " {node}")
    @Filter(CommandWords.PERMISSION_GROUP_REGEX + " {name} " + REVOKE + " {node}")
    @RequirePermission("permission.group.remove")
    public void onRemoveGroupPermission(XiaomingUser user,
                                        @FilterParameter("name") String name,
                                        @FilterParameter("node") String node) {
        PermissionGroup group = permissionManager.getGroup(name);

        if (node.startsWith("-")) {
            user.sendError("{} 并不是一个合理的权限节点哦", node);
            return;
        }

        if (Objects.nonNull(group)) {
            if (!permissionManager.groupHasPermission(group, node)) {
                user.sendMessage("权限组 {} 并不具有 {} 的权限哦", getPermissionGroupName(name), node);
            } else {
                group.removePermission(node);
                getXiaomingBot().getRegularPreserveManager().readySave(permissionManager);
                if (permissionManager.groupHasPermission(group, node)) {
                    List<String> permissions = new ArrayList<>();
                    permissions.add('-' + node);
                    permissions.addAll(group.getPermissions());
                    group.setPermissions(permissions);

                    user.sendMessage("成功移除了权限组 {} 的权限：{}，但是其父类仍具有该权限。" +
                            "小明已经帮你增加了权限节点：-{} 以删除此权限。", getPermissionGroupName(name), node, node);
                } else {
                    user.sendMessage("成功移除了权限组 {} 的权限：{}", getPermissionGroupName(name), node);
                }
            }
        } else {
            user.sendMessage("找不到权限组：{}", name);
        }
    }

    /**
     * 查看用户权限
     */
    @Filter(CommandWords.USER_REGEX + CommandWords.PERMISSION_GROUP_REGEX + " {qq}")
    @RequirePermission("permission.user.look")
    public void onLookUserPermission(XiaomingUser user,
                                     @FilterParameter("qq") long qq) {
        PermissionUserNode userNode = permissionManager.getUserNode(qq);
        if (Objects.isNull(userNode)) {
            user.sendMessage("用户权限信息：\n" +
                    "所属组：{}", getPermissionGroupName("default"));
        } else {
            StringBuilder builder = new StringBuilder("用户权限信息：");
            builder.append("\n").append("所属组：" + getPermissionGroupName(userNode.getGroup()));
            if (Objects.isNull(userNode.getPermissions()) || userNode.getPermissions().isEmpty()) {
                builder.append("\n").append("没有其他特有权限。");
            } else {
                builder.append("\n").append("特有权限（" + userNode.getPermissions().size() + "条）：");
                for (String node : userNode.getPermissions()) {
                    builder.append("\n").append(node);
                }
            }
            user.sendMessage(builder.toString());
        }
    }

    /**
     * 设置继承关系
     */
    @Filter(CommandWords.PERMISSION_GROUP_REGEX + " {super} " + EXTENDS + " {son}")
    @Filter(CommandWords.PERMISSION_GROUP_REGEX + " {son} " + INHERIT + " {super}")
    @RequirePermission("permission.group.extends.new")
    public void onAddGroupSuper(XiaomingUser user,
                                @FilterParameter("super") String superGroupName,
                                @FilterParameter("son") String sonGroupName) {
        final PermissionGroup superGroup = permissionManager.getGroup(superGroupName);
        final PermissionGroup sonGroup = permissionManager.getGroup(sonGroupName);

        if (Objects.isNull(superGroup)) {
            user.sendError("找不到父权限组{}", superGroupName);
            return;
        }
        if (Objects.isNull(sonGroup)) {
            user.sendError("找不到子权限组{}", sonGroupName);
            return;
        }

        if (permissionManager.isSuper(superGroupName, sonGroupName)) {
            user.sendError("{}已经是{}的父类了，无须重复继承", getPermissionGroupName(sonGroupName), getPermissionGroupName(superGroupName));
            return;
        } else if (permissionManager.isSuper(sonGroupName, superGroupName)) {
            user.sendError("{}已经是{}的父类了，无法相互继承", getPermissionGroupName(sonGroupName), getPermissionGroupName(superGroupName));
        } else {
            sonGroup.getSuperGroups().add(superGroupName);
            getXiaomingBot().getRegularPreserveManager().readySave(permissionManager);
            user.sendMessage("成功令{}继承了{}的所有权限", getPermissionGroupName(sonGroupName), getPermissionGroupName(superGroupName));
        }
    }

    /**
     * 取消继承关系
     */
    @Filter(CommandWords.PERMISSION_GROUP_REGEX + " {super} " + CommandWords.CANCEL_REGEX + EXTENDS + " {son}")
    @Filter(CommandWords.PERMISSION_GROUP_REGEX + " {son} " + CommandWords.CANCEL_REGEX + INHERIT + " {super}")
    @RequirePermission("permission.group.extends.cancel")
    public void onRemoveGroupSuper(XiaomingUser user,
                                   @FilterParameter("super") String superGroupName,
                                   @FilterParameter("son") String sonGroupName) {
        final PermissionGroup superGroup = permissionManager.getGroup(superGroupName);
        final PermissionGroup sonGroup = permissionManager.getGroup(sonGroupName);

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
            getXiaomingBot().getRegularPreserveManager().readySave(permissionManager);
            user.sendMessage("成功取消了{}派生{}的联系", getPermissionGroupName(superGroupName), getPermissionGroupName(sonGroupName));
        } else {

            user.sendError("{}并不是{}的父权限组哦", getPermissionGroupName(superGroupName), getPermissionGroupName(sonGroupName));
        }
    }
}