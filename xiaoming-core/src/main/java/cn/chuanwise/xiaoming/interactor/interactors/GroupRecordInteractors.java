package cn.chuanwise.xiaoming.interactor.interactors;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Required;
import cn.chuanwise.xiaoming.group.GroupInformation;
import cn.chuanwise.xiaoming.group.GroupInformationManager;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;

import java.util.List;
import java.util.Set;

/**
 * 小明响应群相关的指令处理器
 * @author Chuanwise
 */
public class GroupRecordInteractors extends SimpleInteractors {
    GroupInformationManager groupInformationManager;

    @Override
    public void onRegister() {
        groupInformationManager = xiaomingBot.getGroupInformationManager();
    }

    @Filter(CommandWords.GROUP + CommandWords.RECORD)
    @Required("group.list")
    public void onListGroups(XiaomingUser user) {
        final Set<GroupInformation> groups = groupInformationManager.getGroups();
        if (groups.isEmpty()) {
            user.sendMessage("{lang.noAnyGroupRecords}");
        } else {
            user.sendMessage("{lang.groupInformations}", groups);
        }
    }

    @Filter(CommandWords.GROUP + " {群号}")
    @Filter(CommandWords.GROUP + CommandWords.INFOMATION + " {群号}")
    @Required("group.look")
    public void onLookGroup(XiaomingUser user, @FilterParameter("群号") GroupInformation groupInformation) {
        user.sendMessage("{lang.groupInformationDetail}", groupInformation);
    }

    @Filter(CommandWords.REMOVE + CommandWords.GROUP + " {群号}")
    @Filter(CommandWords.REMOVE + CommandWords.GROUP + CommandWords.INFOMATION + " {群号}")
    @Required("group.remove")
    public void onRemoveGroup(XiaomingUser user, @FilterParameter("群号") GroupInformation group) {
        user.sendMessage("{lang.groupInformationRemoved}", group);
        groupInformationManager.getGroups().remove(group);
        xiaomingBot.getFileSaver().readyToSave(groupInformationManager);
    }

    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.INFOMATION)
    @Required("group.look")
    public void onLookThisGroup(GroupXiaomingUser user) {
        user.sendMessage("{lang.groupInformationDetail}", user.getGroupInformation());
    }

    @Filter(CommandWords.GROUP + CommandWords.TAG + " {群号} {r:标签}")
    @Required("group.tag.add")
    public void onAddGroupTag(XiaomingUser user,
                              @FilterParameter("群号") GroupInformation groupInformation,
                              @FilterParameter("标签") String tag) {
        if (groupInformation.hasTag(tag)) {
            user.sendError("{lang.groupAlreadyHasThisTag}", groupInformation, tag);
        } else {
            user.sendMessage("{lang.groupTagAdded}", groupInformation, tag);

            groupInformation.addTag(tag);
            xiaomingBot.getFileSaver().readyToSave(groupInformationManager);
        }
    }

    @Filter(CommandWords.REMOVE + CommandWords.GROUP + CommandWords.TAG + " {群号} {r:标签}")
    @Required("group.tag.remove")
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
            xiaomingBot.getFileSaver().readyToSave(groupInformationManager);
        } else {
            user.sendMessage("{lang.groupHadNotTag}", groupInformation, tag);
        }
    }

    @Filter(CommandWords.REMOVE + CommandWords.THIS + CommandWords.GROUP + CommandWords.TAG + " {r:标签}")
    @Required("group.tag.remove")
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
            xiaomingBot.getFileSaver().readyToSave(groupInformationManager);
        } else {
            user.sendMessage("{lang.groupHadNotTag}", groupInformation, tag);
        }
    }

    @Filter(CommandWords.TAG + CommandWords.THIS + CommandWords.GROUP + " {r:标签}")
    @Required("group.tag.add")
    public void onAddGroupTag(GroupXiaomingUser user,
                              @FilterParameter("标签") String tag) {
        final GroupInformation groupInformation = user.getGroupInformation();
        if (groupInformation.hasTag(tag)) {
            user.sendError("{lang.groupAlreadyHasThisTag}", groupInformation, tag);
        } else {
            groupInformation.addTag(tag);
            xiaomingBot.getFileSaver().readyToSave(groupInformationManager);
            user.sendMessage("{lang.groupTagAdded}", groupInformation, tag);
        }
    }

    @Filter(CommandWords.TAGGED + CommandWords.GROUP + " {r:标签}")
    @Required("group.tag.search")
    public void searchGroupsByTag(XiaomingUser user, @FilterParameter("标签") String tag) {
        final List<GroupInformation> informations = groupInformationManager.searchGroupsByTag(tag);
        if (informations.isEmpty()) {
            user.sendError("没有用「" + tag + "」找到任何群信息");
        } else {
            user.sendMessage("用「" + tag + "」找到下列群信息：\n" +
                    CollectionUtil.toIndexString(informations, GroupInformation::getAliasAndCode));
        }
    }
}