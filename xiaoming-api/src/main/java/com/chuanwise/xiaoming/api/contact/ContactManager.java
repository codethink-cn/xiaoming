package com.chuanwise.xiaoming.api.contact;

import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.object.ModuleObject;
import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import com.chuanwise.xiaoming.api.contact.contact.TempContact;
import com.chuanwise.xiaoming.api.util.InteractorUtils;

import java.util.*;

public interface ContactManager extends ModuleObject {
    void clear();

    PrivateContact getPrivateContact(long qq);

    GroupContact getGroupContact(long code);

    TempContact getTempContact(long code, long qq);

    Map<String, List<GroupMessage>> getGroupRecentMessages();

    default GroupMessage nextGroupMessage(String tag, long timeout) {
        return InteractorUtils.waitLastElement(getOrPutGroupRecentMessages(tag), timeout);
    }

    default List<GroupMessage> getGroupRecentMessages(String tag) {
        return getGroupRecentMessages().get(tag);
    }

    default List<GroupMessage> getOrPutGroupRecentMessages(String tag) {
        List<GroupMessage> list = getGroupRecentMessages(tag);
        if (Objects.isNull(list)) {
            list = new LinkedList<>();
            getGroupRecentMessages().put(tag, list);
        }
        return list;
    }

    default boolean sendGroupMessage(long group, String message) {
        final GroupContact groupContact = getGroupContact(group);
        if (Objects.nonNull(groupContact)) {
            groupContact.send(message);
            return true;
        } else {
            return false;
        }
    }

    default boolean sendPrivateMessage(long qq, String message) {
        final PrivateContact privateContact = getPrivateContact(qq);
        if (Objects.nonNull(privateContact)) {
            privateContact.send(message);
            return true;
        } else {
            return false;
        }
    }

    default boolean sendTempMessage(long group, long qq, String message) {
        final GroupContact groupContact = getGroupContact(group);
        if (Objects.nonNull(groupContact)) {
            final TempContact member = groupContact.getMember(qq);
            if (Objects.nonNull(member)) {
                member.send(message);
                return true;
            }
        }
        return false;
    }
}
