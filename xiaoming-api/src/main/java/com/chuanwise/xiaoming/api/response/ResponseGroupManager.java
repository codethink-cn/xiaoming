package com.chuanwise.xiaoming.api.response;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;
import net.mamoe.mirai.contact.Group;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 响应群管理器
 */
public interface ResponseGroupManager extends XiaomingObject, Preservable<File> {
    default ResponseGroup forCode(long group) {
        for (ResponseGroup responseGroup : getGroups()) {
            if (responseGroup.getCode() == group) {
                return responseGroup;
            }
        }
        return null;
    }

    default boolean hasTag(long group, String tag) {
        final ResponseGroup responseGroup = forCode(group);
        if (Objects.nonNull(responseGroup)) {
            return responseGroup.hasTag(tag);
        } else {
            return Objects.equals(group + "", tag) || Objects.equals(tag, "unrecorded");
        }
    }

    /**
     * 获得所有具有若干个标记的群
     * @param tag 若干个标记
     * @return 群
     */
    default Set<ResponseGroup> fromTag(String tag) {
        Set<ResponseGroup> result = new HashSet<>();
        for (ResponseGroup group : getGroups()) {
            if (group.hasTag(tag)) {
                result.add(group);
            }
        }
        return result;
    }

    default void addGroup(ResponseGroup group) {
        getGroups().add(group);
    }

    Set<ResponseGroup> getGroups();

    default void sendMessageToTaggedGroup(String tag, String message) {
        for (ResponseGroup responseGroup : getXiaomingBot().getResponseGroupManager().fromTag("log")) {
            final Group group = getXiaomingBot().getMiraiBot().getGroup(responseGroup.getCode());
            if (Objects.nonNull(group)) {
                group.sendMessage(message);
            }
        }
    }
}
