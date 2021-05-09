package com.chuanwise.xiaoming.core.command.executor;

import com.chuanwise.xiaoming.api.annotation.Command;
import com.chuanwise.xiaoming.api.annotation.CommandParameter;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.response.ResponseGroupManager;

import java.util.Objects;
import java.util.Set;

/**
 * 小明响应群相关的指令处理器
 * @author Chuanwise
 */
public class ResponseGroupCommandExecutor extends CommandExecutorImpl {
    final ResponseGroupManager groupManager;

    public ResponseGroupCommandExecutor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        groupManager = getXiaomingBot().getResponseGroupManager();
    }

    static final String TAG_REGEX = "(标记|标注|tag)";

    @Override
    public String usageStringsPrefix() {
        return CommandWords.GROUP_REGEX;
    }

    public String getGroupName(final ResponseGroup group) {
        if (Objects.isNull(group.getAlias())) {
            return group.getCode() + "";
        } else {
            return group.getAlias() + "（" + group.getCode() + "）";
        }
    }

    @Command(CommandWords.GROUP_REGEX)
    @RequirePermission("group.list")
    public void onListGroups(final XiaomingUser user) {
        final Set<ResponseGroup> groups = groupManager.getGroups();
        if (groups.isEmpty()) {

        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("小明的响应群有 ").append(groups.size()).append("个：");
            for (ResponseGroup group : groups) {
                builder.append("\n")
                        .append(group.getAlias()).append("（" + group.getCode() + "）");
            }
            user.sendMessage(builder.toString());
        }

    }

    @Command(CommandWords.GROUP_REGEX + " {group}")
    @RequirePermission("group.look")
    public void onLookGroup(final XiaomingUser user,
                            @CommandParameter("group") final String groupString) {
        ResponseGroup group;
        if (groupString.matches("\\d+")) {
            group = groupManager.fromCode(Long.parseLong(groupString));
        } else {
            user.sendError("只允许通过群号查看小明的响应群消息哦");
            return;
        }

        if (Objects.isNull(group)) {
            user.sendMessage("这个群并不是小明的响应群");
        } else {
            user.sendMessage("群备注：{}\n" +
                            "群号：{}\n" +
                            "群标记：{}\n" +
                            "屏蔽的插件：{}",
                    Objects.nonNull(group.getAlias()) ? group.getAlias() : "（无）",
                    group.getCode(),
                    group.getTags(),
                    group.getBlockedPlugins());
        }
    }

    @Command(CommandWords.REMOVE_REGEX + CommandWords.GROUP_REGEX + " {group}")
    @RequirePermission("group.remove")
    public void onRemoveGroup(final XiaomingUser user,
                              @CommandParameter("group") final String groupString) {
        ResponseGroup group;
        if (groupString.matches("\\d+")) {
            group = groupManager.fromCode(Long.parseLong(groupString));
        } else {
            user.sendError("只允许通过群号操控小明的响应群哦");
            return;
        }

        if (Objects.isNull(group)) {
            user.sendMessage("这个群并不是小明的响应群哦");
        } else {
            user.sendMessage("成功移除小明响应群：", getGroupName(group));
            groupManager.getGroups().remove(group);
            getXiaomingBot().getRegularPreserveManager().readySave(groupManager);
        }
    }

    @Command(CommandWords.THIS_REGEX + CommandWords.GROUP_REGEX)
    @RequirePermission("group.look")
    public void onLookThisGroup(final GroupXiaomingUser user) {
        ResponseGroup group = groupManager.fromCode(user.getGroupNumber());

        user.sendMessage("本群备注：{}\n" +
                        "群号：{}\n" +
                        "群标记：{}\n" +
                        "屏蔽的插件：{}",
                Objects.nonNull(group.getAlias()) ? group.getAlias() : "（无）",
                group.getCode(),
                group.getTags(),
                group.getBlockedPlugins());
    }

    @Command(CommandWords.THIS_REGEX + CommandWords.GROUP_REGEX)
    public void onListGroupTags(final GroupXiaomingUser user) {
        user.sendMessage("本群群号：{}", user.getGroupNumber());

        if (user.hasPermission("group.tag.list")) {
            final ResponseGroup group = groupManager.fromCode(user.getGroupNumber());
            user.sendMessage("{}的标记有：{}", getGroupName(group), group.getTags());
        }
    }

    @Command(TAG_REGEX + CommandWords.GROUP_REGEX + " {group} " + " {tag}")
    @RequirePermission("group.tag.add")
    public void onAddGroupTag(final XiaomingUser user,
                              @CommandParameter("group") final String groupString,
                              @CommandParameter("tag") final String tag) {
        final ResponseGroup group;
        if (groupString.matches("\\d+")) {
            group = groupManager.fromCode(Long.parseLong(groupString));
        } else {
            user.sendError("找不到响应群：{}", groupString);
            return;
        }
        final Set<String> tags = group.getTags();
        if (tags.contains(tag)) {
            user.sendError("{}已经有这个标记了哦", getGroupName(group));
        } else {
            tags.add(tag);
            getXiaomingBot().getRegularPreserveManager().readySave(groupManager);
            user.sendMessage("成功为{}添加了新的标记：{}", getGroupName(group), tag);
        }
    }

    @Command(CommandWords.THIS_REGEX + CommandWords.GROUP_REGEX + CommandWords.BLOCK_REGEX + " {plugin}")
    @RequirePermission("group.plugin.block")
    public void onBlockPlugin(final GroupXiaomingUser user,
                              @CommandParameter("plugin") final String plugin) {
        ResponseGroup group = groupManager.fromCode(user.getGroupNumber());
        if (group.isBlockPlugin(plugin)) {
            user.sendError("本群已经屏蔽了插件{}", plugin);
        } else {
            group.blockPlugin(plugin);
            getXiaomingBot().getRegularPreserveManager().readySave(groupManager);
            if (getXiaomingBot().getPluginManager().isLoaded(plugin)) {
                user.sendMessage("成功在本群屏蔽了插件{}", plugin);
            } else {
                user.sendError("成功在本群屏蔽了插件{}，但该插件还没有加载", plugin);
            }
        }
    }

    @Command(CommandWords.THIS_REGEX + CommandWords.GROUP_REGEX + CommandWords.UNBLOCK_REGEX + " {plugin}")
    @RequirePermission("group.plugin.unblock")
    public void onUnblockPlugin(final GroupXiaomingUser user,
                                @CommandParameter("plugin") final String plugin) {
        ResponseGroup group = groupManager.fromCode(user.getGroupNumber());
        if (group.isBlockPlugin(plugin)) {
            group.getBlockedPlugins().remove(plugin);
            getXiaomingBot().getRegularPreserveManager().readySave(groupManager);
            if (getXiaomingBot().getPluginManager().isLoaded(plugin)) {
                user.sendMessage("成功在本群取消屏蔽插件{}", plugin);
            } else {
                user.sendError("成功在本群取消屏蔽插件{}，但该插件还没有加载", plugin);
            }
        } else {
            user.sendError("本群还没有屏蔽插件{}", plugin);
        }
    }
}