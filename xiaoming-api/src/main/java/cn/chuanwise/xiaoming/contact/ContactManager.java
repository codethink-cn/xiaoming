package cn.chuanwise.xiaoming.contact;

import cn.chuanwise.toolkit.sized.SizedCopyOnWriteArrayList;
import cn.chuanwise.toolkit.sized.SizedResidentConcurrentHashMap;
import cn.chuanwise.utility.FunctionalUtility;
import cn.chuanwise.utility.MapUtility;
import cn.chuanwise.xiaoming.group.GroupRecord;
import cn.chuanwise.xiaoming.language.Sentence;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.utility.InteractorUtility;
import cn.chuanwise.xiaoming.contact.message.GroupMessage;
import cn.chuanwise.xiaoming.contact.message.MemberMessage;
import cn.chuanwise.xiaoming.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.*;
import java.util.function.Function;

public interface ContactManager extends ModuleObject {
    void clear();

    PrivateContact getBotPrivateContact();

    PrivateContact getPrivateContact(long code);

    GroupContact getGroupContact(long code);

    MemberContact getMemberContact(long group, long code);

    MemberContact getMemberContact(GroupContact groupContact, NormalMember normalMember);

    /** 私聊最近消息 */
    Map<String, List<PrivateMessage>> getPrivateRecentMessages();

    default PrivateMessage nextPrivateMessage(String accountTag, long timeout) {
        return FunctionalUtility.supplyOrDefault(() -> InteractorUtility.waitLastElement(forPrivateMessages(accountTag), timeout), null);
    }

    default List<PrivateMessage> forPrivateMessages(String accountTag) {
        return MapUtility.getOrPutSupply(getPrivateRecentMessages(), accountTag,
                () -> new SizedCopyOnWriteArrayList<>(getXiaomingBot().getConfiguration().getMaxRecentMessageBufferSize()));
    }


    /** 群聊最近消息 */
    default Map<String, List<GroupMessage>> getGroupRecentMessages() {
        return MapUtility.getOrPutSupply(getGroupMemberRecentMessages(), "recorded",
                () -> new SizedResidentConcurrentHashMap<>(getXiaomingBot().getConfiguration().getMaxRecentMessageBufferSize()));
    }

    default GroupMessage nextGroupMessage(String groupTag, long timeout) {
        return FunctionalUtility.supplyOrDefault(() -> InteractorUtility.waitLastElement(forGroupMessages(groupTag), timeout), null);
    }

    default List<GroupMessage> forGroupMessages(String groupTag) {
        return MapUtility.getOrPutSupply(getGroupRecentMessages(), groupTag,
                () -> new SizedCopyOnWriteArrayList<>(getXiaomingBot().getConfiguration().getMaxRecentMessageBufferSize()));
    }

    /** 成员在群聊中的最近消息 */
    Map<String, Map<String, List<GroupMessage>>> getGroupMemberRecentMessages();

    default GroupMessage nextGroupMemberMessage(String groupTag, String accountTag, long timeout) {
        return FunctionalUtility.supplyOrDefault(() -> InteractorUtility.waitLastElement(forGroupMemberMessages(groupTag, accountTag), timeout), null);
    }

    default List<GroupMessage> forGroupMemberMessages(String groupTag, String accountTag) {
        final Map<String, List<GroupMessage>> groupMemberRecentMessages = MapUtility.getOrPutSupply(getGroupMemberRecentMessages(), groupTag,
                () -> new SizedResidentConcurrentHashMap<>(getXiaomingBot().getConfiguration().getMaxRecentGroupMemberMessageBufferQuantity()));
        return MapUtility.getOrPutSupply(groupMemberRecentMessages, accountTag,
                () -> new SizedCopyOnWriteArrayList<>(getXiaomingBot().getConfiguration().getMaxRecentMessageBufferSize()));
    }


    /** 成员在群聊临时会话中的最近消息 */
    Map<String, Map<String, List<MemberMessage>>> getMemberRecentMessages();

    default MemberMessage nextMemberMessage(String groupTag, String accountTag, long timeout) {
        return FunctionalUtility.supplyOrDefault(() -> InteractorUtility.waitLastElement(forMemberMessages(groupTag, accountTag), timeout), null);
    }

    default List<MemberMessage> forMemberMessages(String groupTag, String accountTag) {
        final Map<String, List<MemberMessage>> groupMemberRecentMessages = MapUtility.getOrPutSupply(getMemberRecentMessages(), groupTag,
                () -> new SizedResidentConcurrentHashMap<>(getXiaomingBot().getConfiguration().getMaxRecentGroupMemberMessageBufferQuantity()));
        return MapUtility.getOrPutSupply(groupMemberRecentMessages, accountTag, () -> new SizedCopyOnWriteArrayList<>(getXiaomingBot().getConfiguration().getMaxRecentMessageBufferSize()));
    }

    default boolean sendGroupMessage(long group, String message, Object... arguments) {
        message = getXiaomingBot().getLanguageManager().render(message, variable -> null, arguments);
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

    default boolean sendGroupMessage(String tag, String message, Object... arguments) {
        message = getXiaomingBot().getLanguageManager().render(message, variable -> null, arguments);
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

    default boolean sendGroupMessage(String tag, Sentence sentence, Function<String, Object> externalGetter, Object... arguments) {
        final Set<GroupRecord> groupRecords = getXiaomingBot().getGroupRecordManager().forTag(tag);
        if (groupRecords.isEmpty()) {
            return false;
        } else {
            for (GroupRecord groupRecord : groupRecords) {
                final GroupContact contact = groupRecord.getContact();
                if (Objects.nonNull(contact)) {
                    contact.send(sentence, externalGetter, arguments);
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

    default boolean sendPrivateMessage(long code, String message, Object... arguments) {
        message = getXiaomingBot().getLanguageManager().render(message, variable -> null, arguments);
        final PrivateContact privateContact = getPrivateContact(code);
        if (Objects.nonNull(privateContact)) {
            privateContact.send(message);
            return true;
        } else {
            return false;
        }
    }

    default boolean sendPrivateMessage(long code, Sentence sentence, Function<String, Object> externalGetter, Object... arguments) {
        final PrivateContact privateContact = getPrivateContact(code);
        if (Objects.nonNull(privateContact)) {
            privateContact.send(sentence, externalGetter, arguments);
            return true;
        } else {
            return false;
        }
    }

    default boolean sendPrivateMessage(long code, MessageChain messages) {
        final PrivateContact privateContact = getPrivateContact(code);
        if (Objects.nonNull(privateContact)) {
            privateContact.send(messages);
            return true;
        } else {
            return false;
        }
    }

    default boolean sendMemberMessage(long group, long code, String message, Object... arguments) {
        message = getXiaomingBot().getLanguageManager().render(message, variable -> null, arguments);
        final GroupContact groupContact = getGroupContact(group);
        if (Objects.nonNull(groupContact)) {
            final MemberContact member = groupContact.getMember(code);
            if (Objects.nonNull(member)) {
                member.send(message);
                return true;
            }
        }
        return false;
    }

    default boolean sendMemberMessage(long group, long code, Sentence sentence, Function<String, Object> externalGetter, Object... arguments) {
        final GroupContact groupContact = getGroupContact(group);
        if (Objects.nonNull(groupContact)) {
            final MemberContact member = groupContact.getMember(code);
            if (Objects.nonNull(member)) {
                member.send(sentence, externalGetter, arguments);
                return true;
            }
        }
        return false;
    }

    default boolean sendMemberMessage(long group, long code, MessageChain messages) {
        final GroupContact groupContact = getGroupContact(group);
        if (Objects.nonNull(groupContact)) {
            final MemberContact member = groupContact.getMember(code);
            if (Objects.nonNull(member)) {
                member.send(messages);
                return true;
            }
        }
        return false;
    }
}
