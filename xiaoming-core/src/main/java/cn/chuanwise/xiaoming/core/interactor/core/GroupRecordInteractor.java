package cn.chuanwise.xiaoming.core.interactor.core;

import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.ObjectUtility;
import cn.chuanwise.utility.StringUtility;
import cn.chuanwise.xiaoming.api.annotation.Filter;
import cn.chuanwise.xiaoming.api.annotation.FilterParameter;
import cn.chuanwise.xiaoming.api.annotation.Permission;
import cn.chuanwise.xiaoming.api.annotation.WhenQuiet;
import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.group.GroupRecord;
import cn.chuanwise.xiaoming.api.group.GroupRecordManager;
import cn.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import cn.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;
import cn.chuanwise.xiaoming.api.utility.CommandWords;
import cn.chuanwise.xiaoming.core.group.GroupRecordImpl;
import cn.chuanwise.xiaoming.core.interactor.InteractorImpl;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * 小明响应群相关的指令处理器
 * @author Chuanwise
 */
public class GroupRecordInteractor extends InteractorImpl {
    final GroupRecordManager groupManager;

    public GroupRecordInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        groupManager = getXiaomingBot().getGroupRecordManager();
        setUsageCommandFormat(CommandWords.GROUP + CommandWords.HELP);
    }

    static final String TAG_REGEX = "(标记|标注|tag)";

    public String getGroupName(GroupRecord group) {
        if (Objects.isNull(group.getAlias())) {
            return group.getCode() + "";
        } else {
            return group.getAlias() + "（" + group.getCode() + "）";
        }
    }

    public String getSummary(GroupRecord group) {
        return "备注：" + ObjectUtility.getOrDefault(group, "（无）") + "\n" +
                "群号：" + group.getCodeString() + "\n" +
                "标记：" + StringUtility.chooseFirstNonEmpty(CollectionUtility.toIndexString(group.getTags()), "（无）");
    }

    @Filter(CommandWords.GROUP)
    @Permission("group.list")
    public void onListGroups(XiaomingUser user) {
        final Set<GroupRecord> groups = groupManager.getGroups();
        if (groups.isEmpty()) {
            user.sendMessage("小明没有任何响应群");
        } else {
            user.sendMessage("小明的响应群有：\n" +
                    CollectionUtility.toIndexString(groups, GroupRecord::getAliasAndCode));
        }
    }

    @Filter(CommandWords.GROUP + " {group}")
    @Permission("group.look")
    public void onLookGroup(XiaomingUser user, @FilterParameter("group") GroupRecord group) {
        user.sendMessage("【群记录】\n" + getSummary(group));
    }

    @Filter(CommandWords.REMOVE + CommandWords.GROUP + " {group}")
    @Permission("group.remove")
    public void onRemoveGroup(XiaomingUser user, @FilterParameter("group") GroupRecord group) {
        user.sendMessage("成功移除小明的群记录：" + group.getAliasAndCode());
        groupManager.getGroups().remove(group);
        getXiaomingBot().getScheduler().readySave(groupManager);
    }

    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.INFO)
    @Permission("group.look")
    public void onLookThisGroup(GroupXiaomingUser user) {
        user.sendMessage("【本群记录】\n" + getSummary(user.getGroupRecord()));
    }

    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.TAG)
    @Permission("group.tag.list")
    public void onListGroupTags(GroupXiaomingUser user) {
        final GroupRecord group = user.getGroupRecord();
        final Set<String> tags = group.getTags();
        if (tags.isEmpty()) {
            user.sendMessage("本群没有任何标记");
        } else {
            user.sendMessage("本群的标记有 " + tags.size() + " 个：" + CollectionUtility.toString(tags, "、"));
        }
    }

    @Filter(CommandWords.ENABLE + CommandWords.XIAOMING + " {group}")
    @Permission("core.enable")
    public void onAddGroup(XiaomingUser user, @FilterParameter("group") long group) {
        GroupRecord groupRecord = groupManager.forCode(group);
        if (Objects.nonNull(groupRecord) && groupRecord.hasTag(getXiaomingBot().getConfiguration().getEnableGroupTag())) {
            user.sendError("该群已经是小明的响应群了哦");
            return;
        } else if (Objects.isNull(groupRecord)) {
            final Bot miraiBot = getXiaomingBot().getMiraiBot();
            final Group miraiBotGroup = miraiBot.getGroup(group);
            boolean alreadyIn;

            if (Objects.isNull(miraiBotGroup)) {
                alreadyIn = false;
                groupRecord = new GroupRecordImpl(group);
            } else {
                alreadyIn = true;
                groupRecord = new GroupRecordImpl(group, miraiBotGroup.getName());
            }
            groupRecord.addTag(getXiaomingBot().getConfiguration().getEnableGroupTag());
            groupManager.addGroup(groupRecord);
            if (alreadyIn) {
                user.sendMessage("成功将该群设置为小明的响应群。");
                user.getXiaomingBot().getContactManager().sendGroupMessage(group, getXiaomingBot().getLanguage().getString("new-response-group"));
            } else {
                user.sendMessage("成功将该群设置为小明的响应群，但小明还不在这个群中。");
            }
        } else {
            groupRecord.addTag(getXiaomingBot().getConfiguration().getEnableGroupTag());
            user.sendMessage("成功将该群设置为小明的响应群。");
        }
        getXiaomingBot().getScheduler().readySave(groupManager);
    }

    @Filter(TAG_REGEX + CommandWords.GROUP + " {group} {tag}")
    @Permission("group.tag.add")
    public void onAddGroupTag(XiaomingUser user,
                              @FilterParameter("group") GroupRecord groupRecord,
                              @FilterParameter("tag") String tag) {
        if (groupRecord.hasTag(tag)) {
            user.sendError(groupRecord.getAliasAndCode() + "已经有这个标记了哦");
        } else {
            groupRecord.addTag(tag);
            getXiaomingBot().getScheduler().readySave(groupManager);
            user.sendMessage("成功为" + groupRecord.getAliasAndCode() + "添加了新的标记「{tag}」");
        }
    }

    @Filter(CommandWords.REMOVE + CommandWords.GROUP + TAG_REGEX + " {group} {tag}")
    @Permission("group.tag.remove")
    public void onRemoveGroupTag(XiaomingUser user,
                                 @FilterParameter("group") GroupRecord groupRecord,
                                 @FilterParameter("tag") String tag) {
        if (Arrays.asList("recorded", groupRecord.getCodeString()).contains(tag)) {
            user.sendError("「」是群聊的原生标记，不可以删除");
            return;
        }

        if (groupRecord.hasTag(tag)) {
            groupRecord.removeTag(tag);
            user.sendMessage("成功移除了该群上的标记「{tag}」");
            getXiaomingBot().getScheduler().readySave(groupManager);
        } else {
            user.sendMessage("该群并没有标记「{tag}」");
        }
    }

    @WhenQuiet
    @Filter(CommandWords.REMOVE + CommandWords.THIS + CommandWords.GROUP + TAG_REGEX + " {tag}")
    @Permission("group.tag.remove")
    public void onRemoveGroupTag(GroupXiaomingUser user,
                                 @FilterParameter("tag") String tag) {
        final GroupRecord groupRecord = user.getGroupRecord();
        if (Arrays.asList("recorded", groupRecord.getCodeString()).contains(tag)) {
            user.sendError("「」是群聊的原生标记，不可以删除");
            return;
        }

        if (groupRecord.hasTag(tag)) {
            groupRecord.removeTag(tag);
            user.sendMessage("成功移除本群的标记「{tag}」");
            getXiaomingBot().getScheduler().readySave(groupManager);
        } else {
            user.sendMessage("本群并没有标记「{tag}」");
        }
    }

    @WhenQuiet
    @Filter(TAG_REGEX + CommandWords.THIS + CommandWords.GROUP + " {tag}")
    @Permission("group.tag.add")
    public void onAddGroupTag(GroupXiaomingUser user,
                              @FilterParameter("tag") String tag) {
        final GroupRecord group = user.getGroupRecord();
        if (group.hasTag(tag)) {
            user.sendError("本群已经有这个标记了");
        } else {
            group.addTag(tag);
            getXiaomingBot().getScheduler().readySave(groupManager);
            user.sendMessage("成功为本群添加了新的标记「{tag}」");
        }
    }

    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.BLOCK + " {plugin}")
    @Permission("group.plugin.block")
    public void onBlockPlugin(GroupXiaomingUser user,
                              @FilterParameter("plugin") XiaomingPlugin plugin) {
        GroupRecord group = user.getGroupRecord();
        if (group.isBlockPlugin(plugin)) {
            user.sendError("本群已经屏蔽了插件「{plugin}」");
        } else {
            group.blockPlugin(plugin);
            getXiaomingBot().getScheduler().readySave(groupManager);
            user.sendMessage("成功在本群屏蔽了插件「{plugin}」");
        }
    }

    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.UNBLOCK + " {plugin}")
    @Permission("group.plugin.unblock")
    public void onUnblockPlugin(GroupXiaomingUser user,
                                @FilterParameter("plugin") XiaomingPlugin plugin) {
        final GroupRecord groupRecord = user.getContact().getGroupRecord();
        if (groupRecord.isBlockPlugin(plugin)) {
            groupRecord.unblockPlugin(plugin);
            getXiaomingBot().getScheduler().readySave(groupManager);
            user.sendMessage("成功在本群取消屏蔽插件「{plugin}」");
        } else {
            user.sendError("本群还没有屏蔽插件「plugin」");
        }
    }

    @Override
    public <T> T parseParameter(XiaomingUser user, Class<T> clazz, String parameterName, String currentValue, String defaultValue) {
        final T t = super.parseParameter(user, clazz, parameterName, currentValue, defaultValue);
        if (Objects.nonNull(t)) {
            return t;
        }
        if (Objects.equals(parameterName, "group") && clazz.isAssignableFrom(GroupRecord.class)) {
            if (currentValue.matches("\\d+")) {
                final GroupRecord groupRecord = getXiaomingBot().getGroupRecordManager().forCode(Long.parseLong(currentValue));
                if (Objects.nonNull(groupRecord)) {
                    return ((T) groupRecord);
                } else {
                    user.sendError("没有找到群号为「" + currentValue + "」的记录哦");
                }
            } else {
                user.sendError("「" + currentValue + "」并不是一个合理的群号呢");
            }
        }
        return null;
    }
}