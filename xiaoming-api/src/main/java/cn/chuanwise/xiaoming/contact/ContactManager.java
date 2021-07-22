package cn.chuanwise.xiaoming.contact;

import cn.chuanwise.toolkit.sized.SizedCopyOnWriteArrayList;
import cn.chuanwise.toolkit.sized.SizedResidentConcurrentHashMap;
import cn.chuanwise.xiaoming.group.GroupRecord;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.utility.InteractorUtility;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.xiaoming.contact.message.GroupMessage;
import cn.chuanwise.xiaoming.contact.message.MemberMessage;
import cn.chuanwise.xiaoming.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.*;

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
        return CollectionUtility.getOrPutSupplie(getPrivateRecentMessages(), accountTag,
                () -> new SizedCopyOnWriteArrayList<>(getXiaomingBot().getConfiguration().getMaxRecentMessageBufferSize()));
    }


    /** 群聊最近消息 */
    default Map<String, List<GroupMessage>> getGroupRecentMessages() {
        return CollectionUtility.getOrPutSupplie(getGroupMemberRecentMessages(), "recorded",
                () -> new SizedResidentConcurrentHashMap<>(getXiaomingBot().getConfiguration().getMaxRecentMessageBufferSize()));
    }

    default GroupMessage nextGroupMessage(String groupTag, long timeout) {
        return InteractorUtility.waitLastElement(forGroupMessages(groupTag), timeout);
    }

    default List<GroupMessage> forGroupMessages(String groupTag) {
        return CollectionUtility.getOrPutSupplie(getGroupRecentMessages(), groupTag,
                () -> new SizedCopyOnWriteArrayList<>(getXiaomingBot().getConfiguration().getMaxRecentMessageBufferSize()));
    }


    /** 成员在群聊中的最近消息 */
    Map<String, Map<String, List<GroupMessage>>> getGroupMemberRecentMessages();

    default GroupMessage nextGroupMemberMessage(String groupTag, String accountTag, long timeout) {
        return InteractorUtility.waitLastElement(forGroupMemberMessages(groupTag, accountTag), timeout);
    }

    default List<GroupMessage> forGroupMemberMessages(String groupTag, String accountTag) {
        final Map<String, List<GroupMessage>> groupMemberRecentMessages = CollectionUtility.getOrPutSupplie(getGroupMemberRecentMessages(), groupTag,
                () -> new SizedResidentConcurrentHashMap<>(getXiaomingBot().getConfiguration().getMaxRecentGroupMemberMessageBufferQuantity()));
        return CollectionUtility.getOrPutSupplie(groupMemberRecentMessages, accountTag,
                () -> new SizedCopyOnWriteArrayList<>(getXiaomingBot().getConfiguration().getMaxRecentMessageBufferSize()));
    }


    /** 成员在群聊临时会话中的最近消息 */
    Map<String, Map<String, List<MemberMessage>>> getMemberRecentMessages();

    default MemberMessage nextMemberMessage(String groupTag, String accountTag, long timeout) {
        return InteractorUtility.waitLastElement(forMemberMessages(groupTag, accountTag), timeout);
    }

    default List<MemberMessage> forMemberMessages(String groupTag, String accountTag) {
        final Map<String, List<MemberMessage>> groupMemberRecentMessages = CollectionUtility.getOrPutSupplie(getMemberRecentMessages(), groupTag,
                () -> new SizedResidentConcurrentHashMap<>(getXiaomingBot().getConfiguration().getMaxRecentGroupMemberMessageBufferQuantity()));
        return CollectionUtility.getOrPutSupplie(groupMemberRecentMessages, accountTag, () -> new SizedCopyOnWriteArrayList<>(getXiaomingBot().getConfiguration().getMaxRecentMessageBufferSize()));
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

    default boolean sendGroupMessage(long group, MessageChain messages) {
        final GroupContact groupContact = getGroupContact(group);
        if (Objects.nonNull(groupContact)) {
            groupContact.send(messages);
            return true;
        } else {
            return false;
        }
    }

    default boolean sendGroupMessage(String tag, String message) {
        final Set<GroupRecord> groupRecords = getXiaomingBot().getGroupRecordManager().forTag(tag);
        if (groupRecords.isEmpty()) {
            return false;
        } else {
            for (GroupRecord groupRecord : groupRecords) {
                final GroupContact contact = groupRecord.getContact();
                if (Objects.nonNull(contact)) {
                    contact.send(message);
                }
            }
            return true;
        }
    }

    default boolean sendGroupMessage(String tag, MessageChain messages) {
        final Set<GroupRecord> groupRecords = getXiaomingBot().getGroupRecordManager().forTag(tag);
        if (groupRecords.isEmpty()) {
            return false;
        } else {
            for (GroupRecord groupRecord : groupRecords) {
                final GroupContact contact = groupRecord.getContact();
                if (Objects.nonNull(contact)) {
                    contact.send(messages);
                }
            }
            return true;
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

    default boolean sendPrivateMessage(long qq, MessageChain messages) {
        final PrivateContact privateContact = getPrivateContact(qq);
        if (Objects.nonNull(privateContact)) {
            privateContact.send(messages);
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

    default boolean sendMemberMessage(long group, long qq, MessageChain messages) {
        final GroupContact groupContact = getGroupContact(group);
        if (Objects.nonNull(groupContact)) {
            final MemberContact member = groupContact.getMember(qq);
            if (Objects.nonNull(member)) {
                member.send(messages);
                return true;
            }
        }
        return false;
    }
}
