package cn.chuanwise.xiaoming.api.contact;

import cn.chuanwise.xiaoming.api.object.ModuleObject;
import cn.chuanwise.xiaoming.api.utility.InteractorUtility;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.xiaoming.api.contact.message.GroupMessage;
import cn.chuanwise.xiaoming.api.contact.message.MemberMessage;
import cn.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.api.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.api.contact.contact.MemberContact;
import net.mamoe.mirai.contact.NormalMember;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public interface ContactManager extends ModuleObject {
    void clear();

    PrivateContact getBotPrivateContact();

    PrivateContact getPrivateContact(long qq);

    GroupContact getGroupContact(long code);

    MemberContact getMemberContact(long code, long qq);

    MemberContact getMemberContact(GroupContact groupContact, NormalMember normalMember);


    /** 私聊最近消息 */
    Map<String, List<PrivateMessage>> getPrivateRecentMessages();

    default PrivateMessage nextPrivateMessage(String accountTag, long timeout) {
        return InteractorUtility.waitLastElement(forPrivateMessages(accountTag), timeout);
    }

    default List<PrivateMessage> forPrivateMessages(String accountTag) {
        return CollectionUtility.getOrSupplie(getPrivateRecentMessages(), accountTag, LinkedList::new);
    }


    /** 群聊最近消息 */
    default Map<String, List<GroupMessage>> getGroupRecentMessages() {
        return CollectionUtility.getOrSupplie(getGroupMemberRecentMessages(), "recorded", ConcurrentHashMap::new);
    }

    default GroupMessage nextGroupMessage(String groupTag, long timeout) {
        return InteractorUtility.waitLastElement(forGroupMessages(groupTag), timeout);
    }

    default List<GroupMessage> forGroupMessages(String groupTag) {
        return CollectionUtility.getOrSupplie(getGroupRecentMessages(), groupTag, LinkedList::new);
    }


    /** 成员在群聊中的最近消息 */
    Map<String, Map<String, List<GroupMessage>>> getGroupMemberRecentMessages();

    default GroupMessage nextGroupMemberMessage(String groupTag, String accountTag, long timeout) {
        return InteractorUtility.waitLastElement(forGroupMemberMessages(groupTag, accountTag), timeout);
    }

    default List<GroupMessage> forGroupMemberMessages(String groupTag, String accountTag) {
        final Map<String, List<GroupMessage>> groupMemberRecentMessages = CollectionUtility.getOrSupplie(getGroupMemberRecentMessages(), groupTag, LinkedHashMap::new);
        return CollectionUtility.getOrSupplie(groupMemberRecentMessages, accountTag, LinkedList::new);
    }


    /** 成员在群聊临时会话中的最近消息 */
    Map<String, Map<String, List<MemberMessage>>> getMemberRecentMessages();

    default MemberMessage nextMemberMessage(String groupTag, String accountTag, long timeout) {
        return InteractorUtility.waitLastElement(forMemberMessages(groupTag, accountTag), timeout);
    }

    default List<MemberMessage> forMemberMessages(String groupTag, String accountTag) {
        final Map<String, List<MemberMessage>> groupMemberRecentMessages = CollectionUtility.getOrSupplie(getMemberRecentMessages(), groupTag, LinkedHashMap::new);
        return CollectionUtility.getOrSupplie(groupMemberRecentMessages, accountTag, LinkedList::new);
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

    default boolean sendMemberMessage(long group, long qq, String message) {
        final GroupContact groupContact = getGroupContact(group);
        if (Objects.nonNull(groupContact)) {
            final MemberContact member = groupContact.getMember(qq);
            if (Objects.nonNull(member)) {
                member.send(message);
                return true;
            }
        }
        return false;
    }
}
