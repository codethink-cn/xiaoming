package cn.chuanwise.xiaoming.contact;

import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.group.GroupRecord;
import cn.chuanwise.xiaoming.language.sentence.Sentence;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.MemberXiaomingUser;
import cn.chuanwise.xiaoming.user.PrivateXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ContactManager extends ModuleObject {
    void clear();

    List<MessageEvent> getRecentMessageEvents();

    PrivateContact getBotPrivateContact();

    PrivateContact getPrivateContact(long code);

    GroupContact getGroupContact(long code);

    MemberContact getMemberContact(long group, long code);

    MemberContact getMemberContact(GroupContact groupContact, NormalMember normalMember);

    void onNextMessageEvent(MessageEvent messageEvent);

    Optional<MessageEvent> nextMessageEvent(long timeout, Predicate<MessageEvent> filter) throws InterruptedException;

    default Optional<MessageEvent> nextMessageEvent(long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, e -> true);
    }

    default Optional<MessageEvent> nextGroupMessage(long code, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaomingUser user = messageEvent.getUser();
            return user instanceof GroupXiaomingUser && ((GroupXiaomingUser) user).getGroupCode() == code;
        });
    }

    default Optional<MessageEvent> nextGroupMessage(String tag, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaomingUser user = messageEvent.getUser();
            return user instanceof GroupXiaomingUser && ((GroupXiaomingUser) user).getContact().hasTags(tag);
        });
    }

    default Optional<MessageEvent> nextGroupMemberMessage(long groupCode, long accountCode, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaomingUser user = messageEvent.getUser();
            return user instanceof GroupXiaomingUser && ((GroupXiaomingUser) user).getGroupCode() == groupCode && user.getCode() == accountCode;
        });
    }

    default Optional<MessageEvent> nextGroupMemberMessage(String groupTag, String accountTag, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaomingUser user = messageEvent.getUser();
            return user instanceof GroupXiaomingUser && ((GroupXiaomingUser) user).getContact().hasTag(groupTag) && user.hasTag(accountTag);
        });
    }

    default Optional<MessageEvent> nextGroupMemberMessage(long groupCode, String accountTag, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaomingUser user = messageEvent.getUser();
            return user instanceof GroupXiaomingUser && ((GroupXiaomingUser) user).getGroupCode() == groupCode && user.hasTag(accountTag);
        });
    }

    default Optional<MessageEvent> nextGroupMemberMessage(String groupTag, long accountCode, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaomingUser user = messageEvent.getUser();
            return user instanceof GroupXiaomingUser && ((GroupXiaomingUser) user).getContact().hasTag(groupTag) && user.getCode() == accountCode;
        });
    }

    default Optional<MessageEvent> nextPrivateMessage(long code, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaomingUser user = messageEvent.getUser();
            return user instanceof PrivateXiaomingUser && user.getCode() == code;
        });
    }

    default Optional<MessageEvent> nextPrivateMessage(String tag, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaomingUser user = messageEvent.getUser();
            return user instanceof PrivateXiaomingUser && user.hasTag(tag);
        });
    }

    default Optional<MessageEvent> nextMemberMessage(long code, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaomingUser user = messageEvent.getUser();
            return user instanceof MemberXiaomingUser && ((MemberXiaomingUser) user).getGroupCode() == code;
        });
    }

    default Optional<MessageEvent> nextMemberMessage(String tag, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaomingUser user = messageEvent.getUser();
            return user instanceof MemberXiaomingUser && ((MemberXiaomingUser) user).getGroupContact().hasTag(tag);
        });
    }

    default boolean sendGroupMessage(long group, String message, Object... arguments) {
        message = getXiaomingBot().getLanguageManager().formatAdditional(message, variable -> null, arguments);
        final GroupContact groupContact = getGroupContact(group);
        if (Objects.nonNull(groupContact)) {
            groupContact.sendMessage(message);
            return true;
        } else {
            return false;
        }
    }

    default boolean sendGroupMessage(long group, MessageChain messages) {
        final GroupContact groupContact = getGroupContact(group);
        if (Objects.nonNull(groupContact)) {
            groupContact.sendMessage(messages);
            return true;
        } else {
            return false;
        }
    }

    default boolean sendGroupMessage(String tag, String message, Object... arguments) {
        message = getXiaomingBot().getLanguageManager().formatAdditional(message, variable -> null, arguments);
        final Set<GroupRecord> groupRecords = getXiaomingBot().getGroupRecordManager().forTag(tag);
        if (groupRecords.isEmpty()) {
            return false;
        } else {
            for (GroupRecord groupRecord : groupRecords) {
                final GroupContact contact = groupRecord.getContact();
                if (Objects.nonNull(contact)) {
                    contact.sendMessage(message);
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
                    contact.sendMessage(getXiaomingBot().getLanguageManager().formatAdditional(sentence, externalGetter, arguments));
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
                    contact.sendMessage(messages);
                }
            }
            return true;
        }
    }

    default boolean sendPrivateMessage(long code, String message, Object... arguments) {
        message = getXiaomingBot().getLanguageManager().formatAdditional(message, variable -> null, arguments);
        final PrivateContact privateContact = getPrivateContact(code);
        if (Objects.nonNull(privateContact)) {
            privateContact.sendMessage(message);
            return true;
        } else {
            return false;
        }
    }

    default boolean sendPrivateMessage(long code, Sentence sentence, Function<String, Object> externalGetter, Object... arguments) {
        final PrivateContact privateContact = getPrivateContact(code);
        if (Objects.nonNull(privateContact)) {
            privateContact.sendMessage(getXiaomingBot().getLanguageManager().formatAdditional(sentence, externalGetter, arguments));
            return true;
        } else {
            return false;
        }
    }

    default boolean sendPrivateMessage(long code, MessageChain messages) {
        final PrivateContact privateContact = getPrivateContact(code);
        if (Objects.nonNull(privateContact)) {
            privateContact.sendMessage(messages);
            return true;
        } else {
            return false;
        }
    }

    default boolean sendMemberMessage(long group, long code, String message, Object... arguments) {
        message = getXiaomingBot().getLanguageManager().formatAdditional(message, variable -> null, arguments);
        final GroupContact groupContact = getGroupContact(group);
        if (Objects.nonNull(groupContact)) {
            final MemberContact member = groupContact.getMember(code);
            if (Objects.nonNull(member)) {
                member.sendMessage(message);
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
                member.sendMessage(getXiaomingBot().getLanguageManager().formatAdditional(sentence, externalGetter, arguments));
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
                member.sendMessage(messages);
                return true;
            }
        }
        return false;
    }

//    default List<XiaomingContact> getPrivateContactPossibly(long code) {
//        final List<XiaomingContact> results = new ArrayList<>();
//
//        final PrivateContact privateContact = getPrivateContact(code);
//        if (Objects.nonNull(privateContact)) {
//            results.add(privateContact);
//        }
//
//        for (Group group : getXiaomingBot().getMiraiBot().getGroups()) {
//            final NormalMember member = group.get(code);
//            if (Objects.nonNull(member)) {
//                results.add(Optional.of(getMemberContact(group.getId(), code)));
//            }
//        }
//
//        return results;
//    }

    default Optional<XiaomingContact> sendPrivateMessagePossibly(long code, MessageChain messageChain) {
        final PrivateContact privateContact = getPrivateContact(code);
        if (Objects.nonNull(privateContact)) {
            try {
                privateContact.sendMessage(messageChain);
                return Optional.of(privateContact);
            } catch (Exception ignored) {
            }
        }

        for (Group group : getXiaomingBot().getMiraiBot().getGroups()) {
            final NormalMember member = group.get(code);
            if (Objects.nonNull(member)) {
                try {
                    member.sendMessage(messageChain);
                    return Optional.of(getMemberContact(group.getId(), code));
                } catch (Exception ignored) {
                }
            }
        }

        return Optional.empty();
    }

    default Optional<XiaomingContact> sendPrivateMessagePossibly(long code, String message, Object... arguments) {
        return sendPrivateMessagePossibly(code, MiraiCode.deserializeMiraiCode(getXiaomingBot().getLanguageManager().formatAdditional(message, variable -> null, arguments)));
    }

    default Optional<XiaomingContact> sendPrivateMessagePossibly(long code, Sentence sentence, Function<String, Object> externalGetter, Object... arguments) {
        return sendPrivateMessagePossibly(code, getXiaomingBot().getLanguageManager().formatAdditional(sentence, externalGetter, arguments));
    }
}
