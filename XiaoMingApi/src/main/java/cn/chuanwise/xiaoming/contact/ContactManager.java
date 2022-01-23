package cn.chuanwise.xiaoming.contact;

import cn.chuanwise.util.LambdaUtil;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.event.SendMessageEvent;
import cn.chuanwise.xiaoming.group.GroupInformation;

import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.MemberXiaomingUser;
import cn.chuanwise.xiaoming.user.PrivateXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface ContactManager extends ModuleObject {
    List<MessageEvent> getRecentMessageEvents();

    PrivateContact getBotPrivateContact();

    Optional<PrivateContact> getPrivateContact(long code);

    Optional<GroupContact> getGroupContact(long code);

    Optional<MemberContact> getMemberContact(long groupCode, long accountCode);

    void onNextMessageEvent(MessageEvent messageEvent);

    Optional<MessageEvent> nextMessageEvent(long timeout, Predicate<MessageEvent> filter) throws InterruptedException;

    List<SendMessageEvent> getRecentSentMessageEvents();

    List<SendMessageEvent> getSendMessageList();

    Future<Optional<Message>> readyToSend(SendMessageEvent event);

    default Optional<MessageEvent> nextMessageEvent(long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, LambdaUtil.truePredicate());
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

    default Optional<Message> sendGroupMessage(long group, String message, Object... arguments) {
        message = getXiaomingBot().getLanguageManager().formatAdditional(message, LambdaUtil.nullFunction(), arguments);

        final String finalMessage = message;
        return getGroupContact(group).flatMap(contact -> contact.sendMessage(finalMessage));
    }

    default Optional<Message> sendGroupMessage(long group, MessageChain messages) {
        return getGroupContact(group).flatMap(contact -> contact.sendMessage(messages));
    }

    default List<Message> sendGroupMessage(String tag, String message, Object... arguments) {
        message = getXiaomingBot().getLanguageManager().formatAdditional(message, LambdaUtil.nullFunction(), arguments);

        final String finalMessage = message;
        return getXiaomingBot().getGroupInformationManager()
                .searchGroupsByTag(tag)
                .stream()
                .map(GroupInformation::getContact)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(contact -> contact.sendMessage(finalMessage))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    default List<Message> sendGroupMessage(String tag, MessageChain messages) {
        return getXiaomingBot().getGroupInformationManager()
                .searchGroupsByTag(tag)
                .stream()
                .map(GroupInformation::getContact)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(contact -> contact.sendMessage(messages))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    default Optional<Message> sendPrivateMessage(long code, String message, Object... arguments) {
        final String finalMessage = getXiaomingBot()
                .getLanguageManager()
                .formatAdditional(message, LambdaUtil.nullFunction(), arguments);

        return getPrivateContact(code)
                .flatMap(contact -> contact.sendMessage(finalMessage));
    }

    default Optional<Message> sendPrivateMessage(long code, MessageChain messages) {
        return getPrivateContact(code)
                .flatMap(contact -> contact.sendMessage(messages));
    }

    default Optional<Message> sendMemberMessage(long group, long code, String message, Object... arguments) {
        final String finalMessage = getXiaomingBot().getLanguageManager().formatAdditional(message, LambdaUtil.nullFunction(), arguments);

        return getGroupContact(group)
                .flatMap(contact -> contact.getMember(code))
                .flatMap(member -> member.sendMessage(finalMessage));
    }

    default Optional<Message> sendMemberMessage(long group, long code, MessageChain messages) {
        return getGroupContact(group)
                .flatMap(contact -> contact.getMember(code))
                .flatMap(member -> member.sendMessage(messages));
    }

    List<XiaomingContact> getPrivateContactPossibly(long code);

    List<GroupContact> getGroupContacts();

    List<PrivateContact> getPrivateContacts();

    default Optional<XiaomingContact> sendPrivateMessagePossibly(long code, MessageChain messageChain) {
        // try send private message
        final Optional<PrivateContact> optionalPrivateContact = getPrivateContact(code);
        if (optionalPrivateContact.isPresent()) {
            final PrivateContact privateContact = optionalPrivateContact.get();
            try {
                privateContact.sendMessage(messageChain);
                return Optional.of(privateContact);
            } catch (Exception ignored) {
            }
        }

        // try send group temp message
        for (Group group : getXiaomingBot().getMiraiBot().getGroups()) {
            final NormalMember member = group.get(code);
            if (Objects.nonNull(member)) {
                try {
                    member.sendMessage(messageChain);
                    return Optional.of(getMemberContact(group.getId(), code).orElseThrow(NoSuchElementException::new));
                } catch (Exception ignored) {
                }
            }
        }

        return Optional.empty();
    }

    default Optional<XiaomingContact> sendPrivateMessagePossibly(long code, String message, Object... arguments) {
        final String finalMessage = getXiaomingBot().getLanguageManager().formatAdditional(message, LambdaUtil.nullFunction(), arguments);
        final MessageChain messageChain = MiraiCode.deserializeMiraiCode(finalMessage);

        return sendPrivateMessagePossibly(code, messageChain);
    }

    default Optional<XiaomingContact> sendMessagePossibly(long code, MessageChain messageChain) {
        // try to send private message
        final Optional<XiaomingContact> optionalXiaomingContact = sendPrivateMessagePossibly(code, messageChain);
        if (optionalXiaomingContact.isPresent()) {
            return optionalXiaomingContact;
        }

        // try to send group msg
        messageChain = new At(code).plus(" ").plus(messageChain);
        final ContactList<Group> groups = getXiaomingBot().getMiraiBot().getGroups();
        for (Group group : groups) {
            final NormalMember member = group.get(code);
            if (Objects.nonNull(member)) {
                try {
                    group.sendMessage(messageChain);
                    return Optional.of(getGroupContact(code).orElseThrow(NoSuchElementException::new));
                } catch (Exception ignored) {
                }
            }
        }

        return Optional.empty();
    }

    default Optional<XiaomingContact> sendMessagePossibly(long code, String message, Object... arguments) {
        final String finalMessage = getXiaomingBot().getLanguageManager().formatAdditional(message, LambdaUtil.nullFunction(), arguments);
        final MessageChain messageChain = MiraiCode.deserializeMiraiCode(finalMessage);
        return sendMessagePossibly(code, messageChain);
    }
}
