package cn.chuanwise.xiaoming.group;

import cn.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 响应群管理器
 */
@Getter
public class GroupRecordManagerImpl extends FilePreservableImpl implements GroupRecordManager {
    Set<GroupRecordImpl> groups = new CopyOnWriteArraySet<>();

    @Setter
    transient XiaomingBot xiaomingBot;

    @Override
    public Set<GroupRecord> getGroups() {
        return (Set) groups;
    }

    @Override
    public GroupRecord addGroup(long group, String alias) {
        GroupRecord groupRecord = new GroupRecordImpl(group, alias);
        return addGroup(groupRecord);
    }

    public void setGroups(Set<GroupRecordImpl> groups) {
        this.groups = groups;
        for (GroupRecordImpl group : groups) {
            group.setXiaomingBot(getXiaomingBot());
            group.addTag("recorded");
            group.addTag(String.valueOf(group.code));
        }
    }

    public GroupRecordManagerImpl() {}
}
