package com.chuanwise.xiaoming.api.response;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.toolkit.preservable.Preservable;
import com.chuanwise.xiaoming.api.util.ArgumentUtils;
import net.mamoe.mirai.contact.Group;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
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

    default ResponseGroup addTag(long group, String alias, String tag) {
        ResponseGroup responseGroup = forCode(group);
        if (Objects.isNull(responseGroup)) {
            responseGroup = addGroup(group, alias);
        }
        responseGroup.addTag(tag);
        return responseGroup;
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
    default Set<ResponseGroup> forTag(String tag) {
        Set<ResponseGroup> result = new HashSet<>();
        for (ResponseGroup group : getGroups()) {
            if (group.hasTag(tag)) {
                result.add(group);
            }
        }
        return result;
    }

    default Set<String> getTags(long group) {
        final ResponseGroup responseGroup = forCode(group);
        if (Objects.nonNull(responseGroup)) {
            return responseGroup.getTags();
        } else {
            Set<String> result = new HashSet<>();
            result.add(String.valueOf(group));
            result.add("unrecorded");
            return result;
        }
    }

    ResponseGroup addGroup(long group, String alias);

    default ResponseGroup addGroup(ResponseGroup group) {
        getGroups().add(group);
        group.setXiaomingBot(getXiaomingBot());
        group.addTag("recorded");
        group.addTag(String.valueOf(group.getCode()));
        return group;
    }

    Set<ResponseGroup> getGroups();

    default void sendMessageToTaggedGroup(String tag, String message) {
        final Map<String, Object> values = getXiaomingBot().getLanguage().getValues();
        message = ArgumentUtils.replaceArguments(message, values, getXiaomingBot().getConfiguration().getMaxIterateTime());
        for (ResponseGroup responseGroup : getXiaomingBot().getResponseGroupManager().forTag(tag)) {
            final Group group = getXiaomingBot().getMiraiBot().getGroup(responseGroup.getCode());
            if (Objects.nonNull(group)) {
                group.sendMessage(message);
            }
        }
    }
}
