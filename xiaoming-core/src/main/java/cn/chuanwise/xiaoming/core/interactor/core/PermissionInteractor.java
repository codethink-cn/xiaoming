package cn.chuanwise.xiaoming.core.interactor.core;

import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.ObjectUtility;
import cn.chuanwise.utility.StringUtility;
import cn.chuanwise.xiaoming.api.annotation.Filter;
import cn.chuanwise.xiaoming.api.annotation.FilterParameter;
import cn.chuanwise.xiaoming.api.annotation.Permission;
import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.interactor.Interactor;
import cn.chuanwise.xiaoming.api.interactor.InteractorManager;
import cn.chuanwise.xiaoming.api.interactor.detail.InteractorMethodDetail;
import cn.chuanwise.xiaoming.api.permission.PermissionAccessible;
import cn.chuanwise.xiaoming.api.permission.PermissionGroup;
import cn.chuanwise.xiaoming.api.permission.PermissionManager;
import cn.chuanwise.xiaoming.api.permission.PermissionUserNode;
import cn.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;
import cn.chuanwise.xiaoming.api.utility.CommandWords;
import cn.chuanwise.xiaoming.core.interactor.InteractorImpl;
import cn.chuanwise.xiaoming.core.permission.PermissionGroupImpl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class PermissionInteractor extends InteractorImpl {
    final PermissionManager permissionManager;

    public PermissionInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        permissionManager = getXiaomingBot().getPermissionManager();
        setUsageCommandFormat(CommandWords.PERMISSION + CommandWords.HELP);
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

    public Map<String, String> makeEnvironment(PermissionGroup group) {
        final Map<String, String> map = new HashMap<>();

        map.put("permissionGroup.name", group.getName());
        map.put("permissionGroup.alias", ObjectUtility.getOrDefault(group.getAlias(), "（无备注）"));
        map.put("permissionGroup.superGroups",
                ObjectUtility.getOrDefault(CollectionUtility.toString(group.getSuperGroups(), this::getPermissionGroupName, "、"), "（无父权限组）"));
        map.put("permissionGroup.permissions", ObjectUtility.getOrDefault(CollectionUtility.toIndexString(group.getPermissions()), "（无特有权限）"));
        map.put("permissionGroup.groupPermissions", ObjectUtility.getOrDefault(CollectionUtility.toIndexString(group.getGroupPermissions().entrySet(), entry -> {
            return entry.getKey() + "：" +
                    ObjectUtility.getOrDefault(CollectionUtility.toIndexString(entry.getValue()), "（无权限）");
        }), "（无特有权限）"));

        return map;
    }

    @Override
    public <T> T onParameter(XiaomingUser user, Class<T> clazz, String parameterName, String currentValue, String defaultValue) {
        Object result = super.onParameter(user, clazz, parameterName, currentValue, defaultValue);
        if (Objects.nonNull(result)) {
            return ((T) result);
        }

        if (Objects.equals(parameterName, "permissionGroup") && PermissionGroup.class.isAssignableFrom(clazz)) {
            if (StringUtility.isEmpty(currentValue)) {
                user.sendMessage("告诉{xiaoming}权限组的名字吧");
                currentValue = user.nextInput().serialize();
                user.setProperty("permissionGroup", currentValue);
            }
            final PermissionGroup permissionGroup = permissionManager.getPermissionGroup(currentValue);
            if (Objects.nonNull(permissionGroup)) {
                result = permissionGroup;
            } else {
                user.sendError("{xiaoming}找不到权限组：{}", currentValue);
            }
        }
        return ((T) result);
    }

    {

    }

    /** 新增权限组 */
    @Filter(CommandWords.NEW + CommandWords.PERMISSION_GROUP + " {permissionGroup}")
    @Filter(CommandWords.ADD + CommandWords.PERMISSION_GROUP + " {permissionGroup}")
    @Permission("permission.group.new")
    public void onNewPermissionGroup(XiaomingUser user,
                                     @FilterParameter("permissionGroup") String name) {
        PermissionGroup group = permissionManager.getPermissionGroup(name);
        if (Objects.nonNull(group)) {
            user.sendError("{permissionGroupAlreadyExists}");
        } else {
            PermissionGroup permissionGroup = new PermissionGroupImpl();
            permissionGroup.addSuperGroup(PermissionManager.DEFAULT_PERMISSION_GROUP);

            permissionManager.addGroup(name, permissionGroup);
            getXiaomingBot().getScheduler().readySave(permissionManager);

            user.setProperty("superGroup", getPermissionGroupName(PermissionManager.DEFAULT_PERMISSION_GROUP));
            user.sendMessage("{createPermissionGroupSuccessfully}");
        }
    }

    /**
     * 权限列表
     */
    @Filter(CommandWords.PERMISSION + "(列表|表|list)")
    @Permission("permission.list")
    public void onListPermissions(XiaomingUser user) {
        final InteractorManager interactorManager = getXiaomingBot().getInteractorManager();
        final Set<Interactor> coreInteractors = interactorManager.getCoreInteractors();
        final Map<XiaomingPlugin, Set<Interactor>> pluginInteractors = interactorManager.getPluginInteractors();

        List<String> permissions = new ArrayList<>();
        for (Interactor interactor : coreInteractors) {
            for (InteractorMethodDetail detail : interactor.getMethodDetails()) {
                permissions.addAll(Arrays.asList(detail.getCommandFormat().getPermissions()));
            }
        }
        for (Set<Interactor> value : pluginInteractors.values()) {
            for (Interactor interactor : value) {
                for (InteractorMethodDetail detail : interactor.getMethodDetails()) {
                    permissions.addAll(Arrays.asList(detail.getCommandFormat().getPermissions()));
                }
            }
        }
        Collections.sort(permissions);

        user.setProperty("list", CollectionUtility.toIndexString(permissions));
        user.sendMessage("{allStaticPermissionNodes}");
    }

    /** 设置用户权限组 */
    @Filter(LET + " {qq} {permissionGroup}")
    @Permission("permission.user.let")
    public void onSetUserGroup(XiaomingUser user,
                               @FilterParameter("qq") long qq,
                               @FilterParameter("permissionGroup") String name,
                               @FilterParameter("permissionGroup") PermissionGroup permissionGroup) {
        permissionManager.getOrPutUserNode(qq).setGroup(name);
        user.sendMessage("{setUserPermissionGroupSuccessfully}");
        getXiaomingBot().getScheduler().readySave(permissionManager);
    }

    /** 删除权限组 */
    @Filter(CommandWords.REMOVE + CommandWords.PERMISSION_GROUP + " {permissionGroup}")
    @Permission("permission.group.remove")
    public void onRemovePermissionGroup(XiaomingUser user,
                                        @FilterParameter("permissionGroup") String name,
                                        @FilterParameter("permissionGroup") PermissionGroup permissionGroup) {
        permissionManager.removeGroup(name);
        user.sendMessage("{removePermissionGroupSuccessfully}");
        getXiaomingBot().getScheduler().readySave(permissionManager);
    }

    /** 设置权限组的别名 */
    @Filter(CommandWords.SET + CommandWords.PERMISSION_GROUP + CommandWords.ALIAS + " {permissionGroup} {alias}")
    @Permission("permission.group.alias")
    public void onSetGroupAlias(XiaomingUser user,
                                @FilterParameter("permissionGroup") PermissionGroup permissionGroup,
                                @FilterParameter("alias") String alias) {
        String elderAlias = permissionGroup.getAlias();
        permissionGroup.setAlias(alias);

        if (Objects.isNull(elderAlias)) {
            user.sendMessage("{createAliasForPermissionGroupSuccessfully}");
        } else {
            user.setProperty("elderAlias", elderAlias);
            user.sendMessage("{changeAliasForPermissionGroupSuccessfully}");
        }

        getXiaomingBot().getScheduler().readySave(permissionManager);
    }

    /** 增加权限组群权限 */
    @Filter(CommandWords.ADD + CommandWords.PERMISSION_GROUP + " {permissionGroup} " + CommandWords.GROUP + CommandWords.PERMISSION + " {tag} {node}")
    @Permission("permission.group.add")
    public void onAddGroupGroupPermission(XiaomingUser user,
                                          @FilterParameter("permissionGroup") PermissionGroup permissionGroup,
                                          @FilterParameter("permissionGroup") String groupName,
                                          @FilterParameter("tag") String tag,
                                          @FilterParameter("node") String node) {
        final PermissionAccessible beforeAccessible = permissionManager.permissionGroupAccessible(permissionGroup, tag, node);
        if (beforeAccessible == PermissionAccessible.ACCESSABLE) {
            user.sendWarning("{permissionGroupAlreadyHasPermissionOnGroup}");
        } else {
            permissionGroup.getOrPutGroupPermission(tag).add(node);
            getXiaomingBot().getScheduler().readySave(permissionManager);
            user.sendMessage("{grantPermissionGroupPermissionInGroupSuccessfully}");
        }
    }

    /** 增加用户群权限 */
    @Filter(CommandWords.ADD + CommandWords.USER + " {qq} " + CommandWords.GROUP + CommandWords.PERMISSION + " {tag} {node}")
    @Permission("permission.group.add")
    public void onAddUserGroupPermission(XiaomingUser user,
                                          @FilterParameter("qq") long qq,
                                          @FilterParameter("tag") String tag,
                                          @FilterParameter("node") String node) {
        final PermissionUserNode userNode = permissionManager.getOrPutUserNode(qq);
        final PermissionAccessible beforeAccessible = permissionManager.userAccessible(qq, tag, node);
        if (beforeAccessible == PermissionAccessible.ACCESSABLE) {
            user.sendWarning("{userAlreadyHasPermissionOnGroup}");
        } else {
            userNode.addPermission(node);
            getXiaomingBot().getScheduler().readySave(permissionManager);
            user.sendMessage("{grantUserPermissionInGroupSuccessfully}");
        }
    }

    /**
     * 删除权限组群权限
     */
    @Filter(CommandWords.REMOVE + CommandWords.PERMISSION_GROUP + " {permissionGroup} " + CommandWords.GROUP + CommandWords.PERMISSION + " {tag} {node}")
    @Permission("permission.group.add")
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

                user.sendWarning("{permissionInPermissionGroupRemovedSuccessfullyButDeleteNodeAdded}");
            } else {
                user.sendWarning("{permissionInPermissionGroupRemovedSuccessfully}");
            }
            getXiaomingBot().getScheduler().readySave(permissionManager);
        } else {
            user.sendWarning("{permissionGroupHasNotThisGroupPermission}");
        }
    }

    /**
     * 删除用户群权限
     */
    @Filter(CommandWords.REMOVE + CommandWords.USER + " {qq} " + CommandWords.GROUP + CommandWords.PERMISSION + " {tag} {node}")
    @Permission("permission.group.add")
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

                user.sendWarning("{revokeUserGroupPermissionSuccessfullyButDeleteNodeAdded}");
            } else {
                user.sendWarning("{revokeUserGroupPermissionSuccessfully}");
            }
            getXiaomingBot().getScheduler().readySave(permissionManager);
        } else {
            user.sendWarning("{userHasNotThisGroupPermission}", tag, node);
        }
    }

    /**
     * 查看某一权限组的信息
     */
    @Filter(CommandWords.PERMISSION_GROUP + " {permissionGroup}")
    @Permission("permission.group.look")
    public void onLookPermissionGroup(XiaomingUser user,
                                      @FilterParameter("permissionGroup") String name,
                                      @FilterParameter("permissionGroup") PermissionGroup permissionGroup) {
        final StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        printWriter.println("【权限组信息】");
        printWriter.println("权限组名：" + name);
        printWriter.println("备注：" + (Objects.isNull(permissionGroup.getAlias()) ? "（无）" : permissionGroup.getAlias()));
        printWriter.println("父权限组：" + CollectionUtility.toString(permissionGroup.getSuperGroups()));
        printWriter.println("特有权限：" + CollectionUtility.toString(permissionGroup.getPermissions()));
        printWriter.print("群组权限：" + CollectionUtility.toString(permissionGroup.getGroupPermissions().entrySet(), entry -> {
            final String tag = entry.getKey();
            return tag + "：" + CollectionUtility.toString(entry.getValue(), String::toString);
        }));

        user.sendMessage(stringWriter.toString());
    }

    /**
     * 授权给用户
     */
    @Filter(GRANT + " {qq} {node}")
    @Filter(CommandWords.SET + CommandWords.USE + CommandWords.PERMISSION_GROUP + " {qq} {node}")
    @Permission("permission.user.add.{node}")
    public void onGiveUserPermission(XiaomingUser user,
                                     @FilterParameter("qq") long qq,
                                     @FilterParameter("node") String node) {
        final PermissionUserNode userNode = permissionManager.getOrPutUserNode(qq);
        userNode.addPermission(node);
        user.sendMessage("已授予 {} 权限节点：{}", qq, node);
        getXiaomingBot().getScheduler().readySave(permissionManager);
    }

    /**
     * 增加组权限
     */
    @Filter(CommandWords.NEW + CommandWords.PERMISSION_GROUP + CommandWords.PERMISSION + " {permissionGroup} {node}")
    @Filter(GRANT + CommandWords.PERMISSION_GROUP + " {permissionGroup} {node}")
    @Filter(CommandWords.PERMISSION_GROUP + " {permissionGroup} " + GRANT + " {node}")
    @Permission("permission.group.add")
    public void onAddGroupPermission(XiaomingUser user,
                                     @FilterParameter("permissionGroup") String permissionGroupName,
                                     @FilterParameter("permissionGroup") PermissionGroup permissionGroup,
                                     @FilterParameter("node") String node) {
        if (permissionManager.permissionGroupAccessible(permissionGroup, node) == PermissionAccessible.ACCESSABLE) {
            user.sendMessage("{}已经具有权限：{}了", getPermissionGroupName(permissionGroupName), node);
        } else {
            permissionGroup.addPermission(node);
            getXiaomingBot().getScheduler().readySave(permissionManager);
            user.sendMessage("成功为{}增加了权限：{}", getPermissionGroupName(permissionGroupName), node);
        }
    }

    /**
     * 确认组权限
     */
    @Filter(CommandWords.PERMISSION_GROUP + " {permissionGroup} " + PERMISSION_CONFIRM + " {node}")
    @Permission("permission.group.confirm")
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
    @Permission("permission.group.list")
    public void onListPermissionGroup(XiaomingUser user) {
        final Map<String, PermissionGroup> groups = permissionManager.getGroups();
        user.sendMessage("当前共有 " + groups.size() + " 个权限组：" +
                CollectionUtility.toString(groups.keySet(), this::getPermissionGroupName, "\n"));
    }

    /**
     * 确认玩家权限
     */
    @Filter(CommandWords.USER + " {qq} " + PERMISSION_CONFIRM + " {node}")
    @Permission("permission.user.confirm")
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
    @Permission("permission.user.remove.{node}")
    public void onRemoveUserPermission(XiaomingUser user,
                                       @FilterParameter("qq") long qq,
                                       @FilterParameter("node") String node) {
        if (permissionManager.removeUserPermission(qq, node)) {
            user.sendMessage("已移除该用户的权限：{}", node);
            getXiaomingBot().getScheduler().readySave(permissionManager);
        } else {
            user.sendMessage("该用户并没有权限：{} 哦", node);
        }
    }

    /**
     * 删除组权限
     */
    @Filter(CommandWords.PERMISSION_GROUP + " {permissionGroup} " + CommandWords.REMOVE + " {node}")
    @Filter(CommandWords.PERMISSION_GROUP + " {permissionGroup} " + REVOKE + " {node}")
    @Permission("permission.group.remove")
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
            getXiaomingBot().getScheduler().readySave(permissionManager);
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
    @Permission("permission.user.look")
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
            printWriter.println("特有权限：" + CollectionUtility.toString(userNode.getPermissions(), "\n"));
            printWriter.print("群组权限：" + CollectionUtility.toString(userNode.getGroupPermissions().entrySet(), entry -> {
                final String tag = entry.getKey();
                return tag + "：" + CollectionUtility.toString(entry.getValue(), "，");
            }));

            user.sendMessage(stringWriter.toString());
        }
    }

    /**
     * 设置继承关系
     */
    @Filter(CommandWords.PERMISSION_GROUP + " {super} " + EXTENDS + " {son}")
    @Filter(CommandWords.PERMISSION_GROUP + " {son} " + INHERIT + " {super}")
    @Permission("permission.group.extends.link")
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
            getXiaomingBot().getScheduler().readySave(permissionManager);
            user.sendMessage("成功令{}继承了{}的所有权限", getPermissionGroupName(sonGroupName), getPermissionGroupName(superGroupName));
        }
    }

    /**
     * 取消继承关系
     */
    @Filter(CommandWords.PERMISSION_GROUP + " {super} " + CommandWords.CANCEL + EXTENDS + " {son}")
    @Filter(CommandWords.PERMISSION_GROUP + " {son} " + CommandWords.CANCEL + INHERIT + " {super}")
    @Permission("permission.group.extends.cancel")
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
            getXiaomingBot().getScheduler().readySave(permissionManager);
            user.sendMessage("成功取消了{}派生{}的联系", getPermissionGroupName(superGroupName), getPermissionGroupName(sonGroupName));
        } else {
            user.sendError("{}并不是{}的父权限组哦", getPermissionGroupName(superGroupName), getPermissionGroupName(sonGroupName));
        }
    }
}