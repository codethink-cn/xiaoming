package cn.chuanwise.xiaoming.group;

import cn.chuanwise.api.TagMarkable;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.object.XiaomingObject;
import cn.chuanwise.toolkit.preservable.Preservable;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public interface GroupInformationManager extends XiaomingObject, Preservable {
    @Deprecated
    default GroupInformation forCode(long group) {
        return getGroupInformation(group).orElse(null);
    }

    default Optional<GroupInformation> getGroupInformation(long group) {
        return CollectionUtil.findFirst(getGroups(), groupRecord -> groupRecord.getCode() == group);
    }

    default Set<GroupInformation> searchGroupsByTag(String tag) {
        Set<GroupInformation> result = new HashSet<>();
        for (GroupInformation group : getGroups()) {
            if (group.hasTag(tag)) {
                result.add(group);
            }
        }
        return result;
    }

    default Set<String> getTags(long group) {
        return getGroupInformation(group)
                .map(TagMarkable::getTags)
                .orElseGet(() -> GroupInformation.originalTagsOf(group));
    }

    GroupInformation addGroupInformation(long groupCode);

    boolean addGroupInformation(GroupInformation information);

    Set<GroupInformation> getGroups();

    default String getAliasAndCode(long group) {
        return getGroupInformation(group)
                .map(GroupInformation::getAliasAndCode)
                .orElseGet(() -> String.valueOf(group));
    }

    default Optional<String> getAlias(long group) {
        return getGroupInformation(group)
                .map(GroupInformation::getAlias);
    }
}