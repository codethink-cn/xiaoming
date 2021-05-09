package com.chuanwise.xiaoming.api.response;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 响应群管理器
 */
public interface ResponseGroupManager extends XiaomingObject, Preservable<File> {
    ResponseGroup fromCode(long group);

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

    void addGroup(ResponseGroup group);

    Set<ResponseGroup> getGroups();

    void sendMessageToTaggedGroup(String tag, String message);
}
