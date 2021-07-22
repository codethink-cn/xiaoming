package cn.chuanwise.xiaoming.group;

import cn.chuanwise.utility.ArgumentUtility;
import cn.chuanwise.xiaoming.object.XiaomingObject;
import cn.chuanwise.toolkit.preservable.Preservable;
import net.mamoe.mirai.contact.Group;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 响应群管理器
 */
public interface GroupRecordManager extends XiaomingObject, Preservable<File> {
    default GroupRecord forCode(long group) {
        for (GroupRecord groupRecord : getGroups()) {
            if (groupRecord.getCode() == group) {
                return groupRecord;
            }
        }
        return null;
    }

    default GroupRecord addTag(long group, String alias, String tag) {
        GroupRecord groupRecord = forCode(group);
        if (Objects.isNull(groupRecord)) {
            groupRecord = addGroup(group, alias);
        }
        groupRecord.addTag(tag);
        return groupRecord;
    }

    default boolean hasTag(long group, String tag) {
        final GroupRecord groupRecord = forCode(group);
        if (Objects.nonNull(groupRecord)) {
            return groupRecord.hasTag(tag);
        } else {
            return Objects.equals(group + "", tag) || Objects.equals(tag, "unrecorded");
        }
    }

    /**
     * 获得所有具有若干个标记的群
     * @param tag 若干个标记
     * @return 群
     */
    default Set<GroupRecord> forTag(String tag) {
        Set<GroupRecord> result = new HashSet<>();
        for (GroupRecord group : getGroups()) {
            if (group.hasTag(tag)) {
                result.add(group);
            }
        }
        return result;
    }

    default Set<String> getTags(long group) {
        final GroupRecord groupRecord = forCode(group);
        if (Objects.nonNull(groupRecord)) {
            return groupRecord.getTags();
        } else {
            Set<String> result = new HashSet<>();
            result.add(String.valueOf(group));
            result.add("unrecorded");
            return result;
        }
    }

    GroupRecord addGroup(long group, String alias);

    default GroupRecord addGroup(GroupRecord group) {
        getGroups().add(group);
        group.setXiaomingBot(getXiaomingBot());
        group.addTag("recorded");
        group.addTag(String.valueOf(group.getCode()));
        return group;
    }

    Set<GroupRecord> getGroups();

    default String getAliasAndCode(long group) {
        final GroupRecord groupRecord = forCode(group);
        return Objects.nonNull(groupRecord) ? groupRecord.getAliasAndCode() : String.valueOf(group);
    }
}
