package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.ObjectUtility;
import cn.chuanwise.utility.StringUtility;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.annotation.WhenQuiet;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.group.GroupRecord;
import cn.chuanwise.xiaoming.group.GroupRecordImpl;
import cn.chuanwise.xiaoming.group.GroupRecordManager;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.tag.TagHolder;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.utility.CommandWords;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 小明响应群相关的指令处理器
 * @author Chuanwise
 */
public class GroupRecordInteractors extends SimpleInteractors {
    GroupRecordManager groupManager;

    @Override
    public void onRegister() {
        groupManager = xiaomingBot.getGroupRecordManager();
    }

    @Filter(CommandWords.GROUP + CommandWords.RECORD)
    @Permission("group.list")
    public void onListGroups(XiaomingUser user) {
        final Set<GroupRecord> groups = groupManager.getGroups();
        if (groups.isEmpty()) {
            user.sendMessage("{lang.noAnyGroupRecords}");
        } else {
            user.sendMessage("{lang.groupRecords}", groups);
        }
    }

    @Filter(CommandWords.GROUP + " {群号}")
    @Filter(CommandWords.GROUP + CommandWords.RECORD + " {群号}")
    @Permission("group.look")
    public void onLookGroup(XiaomingUser user, @FilterParameter("群号") GroupRecord groupRecord) {
        user.sendMessage("{lang.groupRecordDetail}", groupRecord);
    }

    @Filter(CommandWords.REMOVE + CommandWords.GROUP + " {群号}")
    @Filter(CommandWords.REMOVE + CommandWords.GROUP + CommandWords.RECORD + " {群号}")
    @Permission("group.remove")
    public void onRemoveGroup(XiaomingUser user, @FilterParameter("群号") GroupRecord group) {
        user.sendMessage("{lang.groupRecordRemoved}", group);
        groupManager.getGroups().remove(group);
        xiaomingBot.getFileSaver().readyToSave(groupManager);
    }

    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.RECORD)
    @Permission("group.look")
    public void onLookThisGroup(GroupXiaomingUser user) {
        user.sendMessage("{lang.groupRecordDetail}", user.getGroupRecord());
    }

    @Filter(CommandWords.ENABLE + CommandWords.XIAOMING + " {群号}")
    @Permission("core.enable")
    public void onAddGroup(XiaomingUser user, @FilterParameter("群号") long group) {
        GroupRecord groupRecord = groupManager.forCode(group);
        if (Objects.nonNull(groupRecord) && groupRecord.hasTag(xiaomingBot.getConfiguration().getEnableGroupTag())) {
            user.sendError("{lang.xiaomingAlreadyEnabledInThatGroup}");
            return;
        } else if (Objects.isNull(groupRecord)) {
            final Bot miraiBot = xiaomingBot.getMiraiBot();
            final Group miraiBotGroup = miraiBot.getGroup(group);
            boolean alreadyIn;

            if (Objects.isNull(miraiBotGroup)) {
                alreadyIn = false;
                groupRecord = new GroupRecordImpl(group);
            } else {
                alreadyIn = true;
                groupRecord = new GroupRecordImpl(group, miraiBotGroup.getName());
            }
            groupRecord.addTag(xiaomingBot.getConfiguration().getEnableGroupTag());
            groupManager.addGroup(groupRecord);
            if (alreadyIn) {
                user.sendMessage("{lang.xiaomingEnabledInThatGroupAndIn}");
                xiaomingBot.getContactManager().sendGroupMessage(group, xiaomingBot.getLanguageManager().getSentenceValue("new-response-group"));
            } else {
                user.sendWarning("{lang.xiaomingEnabledInThatGroupAndNotIn}");
            }
        } else {
            groupRecord.addTag(xiaomingBot.getConfiguration().getEnableGroupTag());
            user.sendMessage("{lang.xiaomingEnabledInThatGroupAndIn}");
        }
        xiaomingBot.getFileSaver().readyToSave(groupManager);
    }

    @Filter(CommandWords.TAG + CommandWords.GROUP + " {群号} {标签}")
    @Permission("group.tag.add")
    public void onAddGroupTag(XiaomingUser user,
                              @FilterParameter("群号") GroupRecord groupRecord,
                              @FilterParameter("标签") String tag) {
        if (groupRecord.hasTag(tag)) {
            user.sendError("{lang.groupAlreadyHasThisTag}", groupRecord, tag);
        } else {
            user.sendMessage("{lang.groupTagAdded}", groupRecord, tag);

            groupRecord.addTag(tag);
            xiaomingBot.getFileSaver().readyToSave(groupManager);
        }
    }

    @Filter(CommandWords.REMOVE + CommandWords.GROUP + CommandWords.TAG + " {群号} {标签}")
    @Permission("group.tag.remove")
    public void onRemoveGroupTag(XiaomingUser user,
                                 @FilterParameter("群号") GroupRecord groupRecord,
                                 @FilterParameter("标签") String tag) {
        if (groupRecord.isOriginalTag(tag)) {
            user.sendError("{lang.canNotRemoveOriginalTag}", tag);
            return;
        }

        if (groupRecord.hasTag(tag)) {
            groupRecord.removeTag(tag);
            user.sendMessage("{lang.groupTagRemoved}", groupRecord, tag);
            xiaomingBot.getFileSaver().readyToSave(groupManager);
        } else {
            user.sendMessage("{lang.groupHadNotTag}", groupRecord, tag);
        }
    }

    @WhenQuiet
    @Filter(CommandWords.REMOVE + CommandWords.THIS + CommandWords.GROUP + CommandWords.TAG + " {标签}")
    @Permission("group.tag.remove")
    public void onRemoveGroupTag(GroupXiaomingUser user,
                                 @FilterParameter("标签") String tag) {
        final GroupRecord groupRecord = user.getGroupRecord();
        if (groupRecord.isOriginalTag(tag)) {
            user.sendError("{lang.canNotRemoveOriginalTag}");
            return;
        }

        if (groupRecord.hasTag(tag)) {
            groupRecord.removeTag(tag);
            user.sendMessage("{lang.groupTagRemoved}", groupRecord, tag);
            xiaomingBot.getFileSaver().readyToSave(groupManager);
        } else {
            user.sendMessage("{lang.groupHadNotTag}", groupRecord, tag);
        }
    }

    @WhenQuiet
    @Filter(CommandWords.TAG + CommandWords.THIS + CommandWords.GROUP + " {标签}")
    @Permission("group.tag.add")
    public void onAddGroupTag(GroupXiaomingUser user,
                              @FilterParameter("标签") String tag) {
        final GroupRecord groupRecord = user.getGroupRecord();
        if (groupRecord.hasTag(tag)) {
            user.sendError("{lang.groupAlreadyHasThisTag}", groupRecord, tag);
        } else {
            groupRecord.addTag(tag);
            xiaomingBot.getFileSaver().readyToSave(groupManager);
            user.sendMessage("{lang.groupTagAdded}", groupRecord, tag);
        }
    }

    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.BLOCK + " {插件名}")
    @Permission("group.plugin.block")
    public void onBlockPlugin(GroupXiaomingUser user,
                              @FilterParameter("插件名") Plugin plugin) {
        final GroupRecord groupRecord = user.getGroupRecord();
        if (groupRecord.isBlockPlugin(plugin)) {
            user.sendError("{lang.groupAlreadyBlockedPlugin}", groupRecord, plugin);
        } else {
            groupRecord.blockPlugin(plugin);
            xiaomingBot.getFileSaver().readyToSave(groupManager);

            user.sendMessage("{lang.groupBlockedPlugin}", groupRecord, plugin);
        }
    }

    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.UNBLOCK + " {插件名}")
    @Permission("group.plugin.unblock")
    public void onUnblockPlugin(GroupXiaomingUser user,
                                @FilterParameter("插件名") Plugin plugin) {
        final GroupRecord groupRecord = user.getContact().getGroupRecord();
        if (groupRecord.isBlockPlugin(plugin)) {
            groupRecord.unblockPlugin(plugin);
            xiaomingBot.getFileSaver().readyToSave(groupManager);

            user.sendMessage("{lang.groupUnblockedPlugin}", groupRecord, plugin);
        } else {
            user.sendError("{lang.groupHadNotBlockedPlugin}", groupRecord, plugin);
        }
    }
}