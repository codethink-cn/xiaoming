package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.*;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CollectionUtils;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.response.ResponseGroupManager;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;
import com.chuanwise.xiaoming.core.response.ResponseGroupImpl;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;

import java.util.Objects;
import java.util.Set;

/**
 * 小明响应群相关的指令处理器
 * @author Chuanwise
 */
public class ResponseGroupCommandInteractor extends CommandInteractorImpl {
    final ResponseGroupManager groupManager;

    public ResponseGroupCommandInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        groupManager = getXiaomingBot().getResponseGroupManager();
        enableUsageCommand(CommandWords.GROUP);
    }

    static final String TAG_REGEX = "(标记|标注|tag)";

    public String getGroupName(ResponseGroup group) {
        if (Objects.isNull(group.getAlias())) {
            return group.getCode() + "";
        } else {
            return group.getAlias() + "（" + group.getCode() + "）";
        }
    }

    @Filter(CommandWords.GROUP)
    @Require("group.list")
    public void onListGroups(XiaomingUser user) {
        final Set<ResponseGroup> groups = groupManager.getGroups();
        if (groups.isEmpty()) {
            user.sendMessage("小明没有任何响应群");
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

    @Filter(CommandWords.GROUP + " {group}")
    @Require("group.look")
    public void onLookGroup(XiaomingUser user, @FilterParameter("group") String groupString) {
        ResponseGroup group;
        if (groupString.matches("\\d+")) {
            group = groupManager.forCode(Long.parseLong(groupString));
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

    @Filter(CommandWords.REMOVE + CommandWords.GROUP + " {group}")
    @Require("group.remove")
    public void onRemoveGroup(XiaomingUser user, @FilterParameter("group") String groupString) {
        ResponseGroup group;
        if (groupString.matches("\\d+")) {
            group = groupManager.forCode(Long.parseLong(groupString));
        } else {
            user.sendError("只允许通过群号操控小明的响应群哦");
            return;
        }

        if (Objects.isNull(group)) {
            user.sendMessage("这个群并不是小明的响应群哦");
        } else {
            user.sendMessage("成功移除小明响应群：", getGroupName(group));
            groupManager.getGroups().remove(group);
            getXiaomingBot().getScheduler().readySave(groupManager);
        }
    }

    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.INFO)
    @Require("group.look")
    public void onLookThisGroup(GroupXiaomingUser user) {
        ResponseGroup group = user.getResponseGroup();

        user.sendMessage("本群备注：{}\n" +
                        "群号：{}\n" +
                        "群标记：{}\n" +
                        "屏蔽的插件：{}",
                Objects.nonNull(group.getAlias()) ? group.getAlias() : "（无）",
                group.getCode(),
                group.getTags(),
                group.getBlockedPlugins());
    }

    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.TAG)
    @Require("group.tag.list")
    public void onListGroupTags(GroupXiaomingUser user) {
        final ResponseGroup group = user.getResponseGroup();
        final Set<String> tags = group.getTags();
        user.sendMessage("{}的所有标记有：{}", group.getCompleteName(), CollectionUtils.getSummary(tags, String::toString, "", "", "、"));
    }

    @Filter(CommandWords.NEW + CommandWords.GROUP + " {group}")
    @Require("group.add")
    public void onAddGroup(XiaomingUser user, @FilterParameter("group") String groupString) {
        final long group;
        if (groupString.matches("\\d+")) {
            group = Long.parseLong(groupString);
        } else {
            user.sendError("找不到响应群：{}", groupString);
            return;
        }
        ResponseGroup responseGroup = groupManager.forCode(group);
        if (Objects.nonNull(responseGroup) && responseGroup.hasTag("enable")) {
            user.sendError("该群已经是小明的响应群了哦");
            return;
        } else if (Objects.isNull(responseGroup)) {
            final Bot miraiBot = getXiaomingBot().getMiraiBot();
            final Group miraiBotGroup = miraiBot.getGroup(group);
            boolean alreadyIn;

            if (Objects.isNull(miraiBotGroup)) {
                alreadyIn = false;
                responseGroup = new ResponseGroupImpl(group);
            } else {
                alreadyIn = true;
                responseGroup = new ResponseGroupImpl(group, miraiBotGroup.getName());
            }
            responseGroup.addTag("enable");
            groupManager.addGroup(responseGroup);
            if (alreadyIn) {
                user.sendMessage("成功将该群设置为小明的响应群。");
                user.getXiaomingBot().getContactManager().sendGroupMessage(group, getXiaomingBot().getLanguage().getString("new-response-group"));
            } else {
                user.sendMessage("成功将该群设置为小明的响应群，但小明还不在这个群中。");
            }
        } else {
            responseGroup.addTag("enable");
            user.sendMessage("成功将该群设置为小明的响应群。");
        }
        getXiaomingBot().getScheduler().readySave(groupManager);
    }

    @Filter(TAG_REGEX + CommandWords.GROUP + " {group} {tag}")
    @Require("group.tag.add")
    public void onAddGroupTag(XiaomingUser user,
                              @FilterParameter("group") String groupString,
                              @FilterParameter("tag") String tag) {
        final ResponseGroup group;
        if (groupString.matches("\\d+")) {
            group = groupManager.forCode(Long.parseLong(groupString));
        } else {
            user.sendError("找不到响应群：{}", groupString);
            return;
        }
        final Set<String> tags = group.getTags();
        if (tags.contains(tag)) {
            user.sendError("{}已经有这个标记了哦", getGroupName(group));
        } else {
            tags.add(tag);
            getXiaomingBot().getScheduler().readySave(groupManager);
            user.sendMessage("成功为{}添加了新的标记：{}", getGroupName(group), tag);
        }
    }

    @Filter(CommandWords.REMOVE + CommandWords.GROUP + TAG_REGEX + " {group} {tag}")
    @Require("group.tag.remove")
    public void onRemoveGroupTag(XiaomingUser user,
                                 @FilterParameter("group") String groupString,
                                 @FilterParameter("tag") String tag) {
        final ResponseGroup group;
        if (groupString.matches("\\d+")) {
            group = groupManager.forCode(Long.parseLong(groupString));
        } else {
            user.sendError("找不到响应群：{}", groupString);
            return;
        }
        final Set<String> tags = group.getTags();
        if (tags.contains(tag)) {
            tags.remove(tag);
            user.sendMessage("成功移除了在该群上的标记：{}", tag);
            getXiaomingBot().getScheduler().readySave(groupManager);
        } else {
            user.sendMessage("该群并没有标记：{}", tag);
        }
    }

    @WhenQuiet
    @Filter(CommandWords.REMOVE + CommandWords.THIS + CommandWords.GROUP + TAG_REGEX + " {tag}")
    @Require("group.tag.remove")
    public void onRemoveGroupTag(GroupXiaomingUser user,
                                 @FilterParameter("tag") String tag) {
        final ResponseGroup group = user.getResponseGroup();
        final Set<String> tags = group.getTags();
        if (tags.contains(tag)) {
            tags.remove(tag);
            user.sendMessage("成功移除本群的标记：{}", tag);
            getXiaomingBot().getScheduler().readySave(groupManager);
        } else {
            user.sendMessage("本群并没有标记：{}", tag);
        }
    }

    @WhenQuiet
    @Filter(TAG_REGEX + CommandWords.THIS + CommandWords.GROUP + " {tag}")
    @Require("group.tag.add")
    public void onAddGroupTag(GroupXiaomingUser user,
                              @FilterParameter("tag") String tag) {
        final ResponseGroup group = user.getResponseGroup();
        final Set<String> tags = group.getTags();
        if (tags.contains(tag)) {
            user.sendError("本群已经有这个标记了哦");
        } else {
            tags.add(tag);
            getXiaomingBot().getScheduler().readySave(groupManager);
            user.sendMessage("成功为本群添加了新的标记：{}", tag);
        }
    }

    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.BLOCK + " {plugin}")
    @Require("group.plugin.block")
    public void onBlockPlugin(GroupXiaomingUser user,
                              @FilterParameter("plugin") String plugin) {
        ResponseGroup group = user.getResponseGroup();
        if (group.isBlockPlugin(plugin)) {
            user.sendError("本群已经屏蔽了插件{}", plugin);
        } else {
            group.blockPlugin(plugin);
            getXiaomingBot().getScheduler().readySave(groupManager);
            if (getXiaomingBot().getPluginManager().isLoaded(plugin)) {
                user.sendMessage("成功在本群屏蔽了插件{}", plugin);
            } else {
                user.sendError("成功在本群屏蔽了插件{}，但该插件还没有加载", plugin);
            }
        }
    }

    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.UNBLOCK + " {plugin}")
    @Require("group.plugin.unblock")
    public void onUnblockPlugin(GroupXiaomingUser user,
                                @FilterParameter("plugin") String plugin) {
        ResponseGroup group = user.getResponseGroup();
        if (group.isBlockPlugin(plugin)) {
            group.getBlockedPlugins().remove(plugin);
            getXiaomingBot().getScheduler().readySave(groupManager);
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