package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.group.GroupInformation;
import cn.chuanwise.xiaoming.group.GroupInformationImpl;
import cn.chuanwise.xiaoming.group.GroupInformationManager;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;

import java.util.Objects;
import java.util.Set;

/**
 * 小明响应群相关的指令处理器
 * @author Chuanwise
 */
public class GroupRecordInteractors extends SimpleInteractors {
    GroupInformationManager groupManager;

    @Override
    public void onRegister() {
        groupManager = xiaomingBot.getGroupInformationManager();
    }

    @Filter(CommandWords.GROUP + CommandWords.RECORD)
    @Permission("group.list")
    public void onListGroups(XiaomingUser user) {
        final Set<GroupInformation> groups = groupManager.getGroups();
        if (groups.isEmpty()) {
            user.sendMessage("{lang.noAnyGroupRecords}");
        } else {
            user.sendMessage("{lang.groupRecords}", groups);
        }
    }

    @Filter(CommandWords.GROUP + " {群号}")
    @Filter(CommandWords.GROUP + CommandWords.RECORD + " {群号}")
    @Permission("group.look")
    public void onLookGroup(XiaomingUser user, @FilterParameter("群号") GroupInformation groupInformation) {
        user.sendMessage("{lang.groupRecordDetail}", groupInformation);
    }

    @Filter(CommandWords.REMOVE + CommandWords.GROUP + " {群号}")
    @Filter(CommandWords.REMOVE + CommandWords.GROUP + CommandWords.RECORD + " {群号}")
    @Permission("group.remove")
    public void onRemoveGroup(XiaomingUser user, @FilterParameter("群号") GroupInformation group) {
        user.sendMessage("{lang.groupRecordRemoved}", group);
        groupManager.getGroups().remove(group);
        xiaomingBot.getFileSaver().readyToSave(groupManager);
    }

    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.RECORD)
    @Permission("group.look")
    public void onLookThisGroup(GroupXiaomingUser user) {
        user.sendMessage("{lang.groupRecordDetail}", user.getGroupInformation());
    }

    @Filter(CommandWords.TAG + CommandWords.GROUP + " {群号} {标签}")
    @Permission("group.tag.add")
    public void onAddGroupTag(XiaomingUser user,
                              @FilterParameter("群号") GroupInformation groupInformation,
                              @FilterParameter("标签") String tag) {
        if (groupInformation.hasTag(tag)) {
            user.sendError("{lang.groupAlreadyHasThisTag}", groupInformation, tag);
        } else {
            user.sendMessage("{lang.groupTagAdded}", groupInformation, tag);

            groupInformation.addTag(tag);
            xiaomingBot.getFileSaver().readyToSave(groupManager);
        }
    }

    @Filter(CommandWords.REMOVE + CommandWords.GROUP + CommandWords.TAG + " {群号} {标签}")
    @Permission("group.tag.remove")
    public void onRemoveGroupTag(XiaomingUser user,
                                 @FilterParameter("群号") GroupInformation groupInformation,
                                 @FilterParameter("标签") String tag) {
        if (groupInformation.isOriginalTag(tag)) {
            user.sendError("{lang.canNotRemoveOriginalTag}", tag);
            return;
        }

        if (groupInformation.hasTag(tag)) {
            groupInformation.removeTag(tag);
            user.sendMessage("{lang.groupTagRemoved}", groupInformation, tag);
            xiaomingBot.getFileSaver().readyToSave(groupManager);
        } else {
            user.sendMessage("{lang.groupHadNotTag}", groupInformation, tag);
        }
    }

    @Filter(CommandWords.REMOVE + CommandWords.THIS + CommandWords.GROUP + CommandWords.TAG + " {标签}")
    @Permission("group.tag.remove")
    public void onRemoveGroupTag(GroupXiaomingUser user,
                                 @FilterParameter("标签") String tag) {
        final GroupInformation groupInformation = user.getGroupInformation();
        if (groupInformation.isOriginalTag(tag)) {
            user.sendError("{lang.canNotRemoveOriginalTag}");
            return;
        }

        if (groupInformation.hasTag(tag)) {
            groupInformation.removeTag(tag);
            user.sendMessage("{lang.groupTagRemoved}", groupInformation, tag);
            xiaomingBot.getFileSaver().readyToSave(groupManager);
        } else {
            user.sendMessage("{lang.groupHadNotTag}", groupInformation, tag);
        }
    }

    @Filter(CommandWords.TAG + CommandWords.THIS + CommandWords.GROUP + " {标签}")
    @Permission("group.tag.add")
    public void onAddGroupTag(GroupXiaomingUser user,
                              @FilterParameter("标签") String tag) {
        final GroupInformation groupInformation = user.getGroupInformation();
        if (groupInformation.hasTag(tag)) {
            user.sendError("{lang.groupAlreadyHasThisTag}", groupInformation, tag);
        } else {
            groupInformation.addTag(tag);
            xiaomingBot.getFileSaver().readyToSave(groupManager);
            user.sendMessage("{lang.groupTagAdded}", groupInformation, tag);
        }
    }
}