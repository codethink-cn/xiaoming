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

    @Filter(CommandWords.GROUP + CommandWords.INFO)
    @Required("core.group.list")
    public void onListGroups(XiaomingUser user) {
        final Set<GroupInformation> groups = groupInformationManager.getGroups();
        if (groups.isEmpty()) {
            user.sendMessage("小明没有记录任何群信息");
        } else {
            user.sendMessage("群信息：\n" +
                    CollectionUtil.toIndexString(groups, GroupInformation::getAliasAndCode));
        }
    }

    @Filter(CommandWords.GROUP + " {群号}")
    @Filter(CommandWords.GROUP + CommandWords.INFO + " {群号}")
    @Required("core.group.look")
    public void groupInfo(XiaomingUser user, @FilterParameter("群号") GroupInformation groupInfo) {
        user.sendMessage("「群记录信息」\n" +
                "备注：" + groupInfo.getAlias() + "\n" +
                "标签：" + CollectionUtil.toString(groupInfo.getTags()));
    }

    @Filter(CommandWords.REMOVE + CommandWords.GROUP + " {群号}")
    @Filter(CommandWords.REMOVE + CommandWords.GROUP + CommandWords.INFO + " {群号}")
    @Required("core.group.remove")
    public void removeGroupInfo(XiaomingUser user, @FilterParameter("群号") GroupInformation group) {
        user.sendMessage("成功删除群信息「" + group.getAliasAndCode() + "」");
        groupInformationManager.getGroups().remove(group);
        xiaomingBot.getFileSaver().readyToSave(groupInformationManager);
    }

    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.INFO)
    @Required("core.group.look")
    public void thisGroupInfo(GroupXiaomingUser user) {
        final GroupInformation info = user.getGroupInformation();
        user.sendMessage("「本群记录信息」\n" +
                "备注：" + info.getAlias() + "\n" +
                "标签：" + CollectionUtil.toString(info.getTags()));
    }

    @Filter(CommandWords.GROUP + CommandWords.TAG + " {群号} {标签}")
    @Required("core.group.tag.add")
    public void addGroupTag(XiaomingUser user,
                            @FilterParameter("群号") GroupInformation info,
                            @FilterParameter("标签") String groupTag) {
        if (info.hasTag(groupTag)) {
            user.sendError("群聊「" + info.getAliasAndCode() + "」已有标签「" + groupTag + "」");
        } else {
            info.addTag(groupTag);
            xiaomingBot.getFileSaver().readyToSave(groupInformationManager);

            user.sendMessage("成功为群聊「" + info.getAliasAndCode() + "」添加了标签「" + groupTag + "」");
        }
    }

    @Filter(CommandWords.REMOVE + CommandWords.GROUP + CommandWords.TAG + " {群号} {r:标签}")
    @Required("core.group.tag.remove")
    public void removeGroupTag(XiaomingUser user,
                               @FilterParameter("群号") GroupInformation info,
                               @FilterParameter("标签") String groupTag) {
        if (info.isOriginalTag(groupTag)) {
            user.sendError("「" + groupTag + "」是原生标记，不能删除");
            return;
        }

        if (info.hasTag(groupTag)) {
            info.removeTag(groupTag);
            xiaomingBot.getFileSaver().readyToSave(groupInformationManager);
            user.sendMessage("成功删除群聊「" + info.getAliasAndCode() + "」的标签「" + groupTag + "」");
        } else {
            user.sendMessage("群聊「" + info.getAliasAndCode() + "」还没有标签「" + groupTag + "」");
        }
    }

    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.TAG + " {群号} {标签}")
    @Required("core.group.tag.add")
    public void addThisGroupTag(GroupXiaomingUser user,
                            @FilterParameter("标签") String groupTag) {
        final GroupInformation info = user.getGroupInformation();
        if (info.hasTag(groupTag)) {
            user.sendError("本群已有标签「" + groupTag + "」");
        } else {
            info.addTag(groupTag);
            xiaomingBot.getFileSaver().readyToSave(groupInformationManager);

            user.sendMessage("成功本群添加了标签「" + groupTag + "」");
        }
    }

    @Filter(CommandWords.THIS + CommandWords.REMOVE + CommandWords.GROUP + CommandWords.TAG + " {群号} {r:标签}")
    @Required("core.group.tag.remove")
    public void removeThisGroupTag(GroupXiaomingUser user,
                               @FilterParameter("标签") String groupTag) {
        final GroupInformation info = user.getGroupInformation();
        if (info.isOriginalTag(groupTag)) {
            user.sendError("「" + groupTag + "」是原生标记，不能删除");
            return;
        }

        if (info.hasTag(groupTag)) {
            info.removeTag(groupTag);
            xiaomingBot.getFileSaver().readyToSave(groupInformationManager);
            user.sendMessage("成功删除本群的标签「" + groupTag + "」");
        } else {
            user.sendMessage("本群还没有标签「" + groupTag + "」");
        }
    }

    @Filter(CommandWords.TAGGED + CommandWords.GROUP + " {r:标签}")
    @Required("core.group.tag.search")
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