package cn.chuanwise.xiaoming.contact;

import cn.chuanwise.exception.UnsupportedVersionException;
import cn.chuanwise.toolkit.container.Container;
import cn.chuanwise.toolkit.sized.SizedCopyOnWriteArrayList;
import cn.chuanwise.util.MapUtil;
import cn.chuanwise.util.ObjectUtil;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.contact.*;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.contact.message.MessageImpl;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.event.SendMessageEvent;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import lombok.AccessLevel;
import lombok.Getter;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class ContactManagerImpl extends ModuleObjectImpl implements ContactManager {
    final Map<Long, PrivateContact> privateContacts = new HashMap<>();

    final Map<Long, Map<Long, MemberContact>> memberContacts = new HashMap<>();

    final Map<Long, GroupContact> groupContacts = new HashMap<>();

    final List<MessageEvent> recentMessageEvents;

    final List<SendMessageEvent> recentSentMessageEvents;

    final List<SendMessageEvent> sendMessageList = new CopyOnWriteArrayList<>();

    public ContactManagerImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
        recentMessageEvents = Collections.synchronizedList(new SizedCopyOnWriteArrayList<>(xiaomingBot.getConfiguration().getMaxRecentMessageBufferSize()));
        recentSentMessageEvents = Collections.synchronizedList(new SizedCopyOnWriteArrayList<>(xiaomingBot.getConfiguration().getMaxRecentMessageBufferSize()));
    }

    final Container<Thread> sendMessageLoop = Container.empty();

    @Override
    public Future<Optional<Message>> readyToSend(SendMessageEvent event) {
        recentSentMessageEvents.add(event);
        sendMessageList.add(event);

        sendMessageLoop.ifHasNotValue(() -> getXiaomingBot().getScheduler().run(() -> {
            try {
                sendMessageLoop.setValue(Thread.currentThread());

                while (!sendMessageList.isEmpty()) {
                    final SendMessageEvent cursor = sendMessageList.remove(0);

                    if (!cursor.isCancelled()) {
                        final XiaomingContact contact = cursor.getTarget();
                        final Contact miraiContact = contact.getMiraiContact();

                        final MessageReceipt messageReceipt = miraiContact.sendMessage(cursor.getMessageChain());
                        final OnlineMessageSource.Outgoing source = messageReceipt.getSource();

                        cursor.getMessageContainer().setValue(new MessageImpl(xiaomingBot, source.getOriginalMessage(), source.getTime()));
                    }

                    synchronized (cursor) {
                        cursor.notifyAll();
                    }

                    try {
                        Thread.sleep(Math.max(getXiaomingBot().getConfiguration().getSendMessagePeriod(), 0));
                    } catch (InterruptedException exception) {
                        getLogger().error("发送消息循环被打断", exception);
                        return;
                    }
                }
            } finally {
                sendMessageLoop.clear();
            }
        }));

        return getXiaomingBot().getScheduler().run(() -> {
            switch (ObjectUtil.wait(event)) {
                case NOTIFY:
                    return event.getMessageContainer().toOptional();
                case TIMEOUT:
                    throw new IllegalStateException();
                default:
                    throw new UnsupportedVersionException();
            }
        });
    }

    @Override
    public List<SendMessageEvent> getSendMessageList() {
        return Collections.unmodifiableList(sendMessageList);
    }

    @Override
    public List<SendMessageEvent> getRecentSentMessageEvents() {
        return Collections.unmodifiableList(recentSentMessageEvents);
    }

    @Override
    public List<MessageEvent> getRecentMessageEvents() {
        return Collections.unmodifiableList(recentMessageEvents);
    }

    @Getter(AccessLevel.NONE)
    final Object recentMessageConditionalVariable = new Object();

    @Override
    public Optional<MessageEvent> nextMessageEvent(long timeout, Predicate<MessageEvent> filter) throws InterruptedException {
        switch (ObjectUtil.wait(recentMessageConditionalVariable, timeout, () -> filter.test(recentMessageEvents.get(recentMessageEvents.size() - 1)))) {
            case NOTIFY:
                return Optional.of(recentMessageEvents.get(recentMessageEvents.size() - 1));
            case TIMEOUT:
                return Optional.empty();
            default:
                throw new UnsupportedVersionException();
        }
    }

    @Override
    public void clear() {
        privateContacts.clear();
        memberContacts.clear();
        groupContacts.clear();
    }

    @Override
    public PrivateContact getBotPrivateContact() {
        final Bot miraiBot = getXiaomingBot().getMiraiBot();

        final long qq = miraiBot.getId();
        PrivateContact privateContact = privateContacts.get(qq);
        Friend friend = miraiBot.getAsFriend();

        // 没有记录过，就创建新的记录
        if (Objects.isNull(privateContact) && Objects.nonNull(friend)) {
            privateContact = new PrivateContactImpl(getXiaomingBot(), friend);
            privateContacts.put(qq, privateContact);
        }

        // 记录过但是本次 get 不到，说明人没了
        if (Objects.isNull(friend) && Objects.nonNull(privateContact)) {
            privateContacts.remove(qq);
            privateContact = null;
        }

        return privateContact;
    }

    @Override
    public Optional<PrivateContact> getPrivateContact(long code) {
        PrivateContact privateContact = privateContacts.get(code);
        Friend friend = getXiaomingBot().getMiraiBot().getFriend(code);

        // 没有记录过，就创建新的记录
        if (Objects.isNull(privateContact) && Objects.nonNull(friend)) {
            privateContact = new PrivateContactImpl(getXiaomingBot(), friend);
            privateContacts.put(code, privateContact);
        }

        // 记录过但是本次 get 不到，说明人没了
        if (Objects.isNull(friend) && Objects.nonNull(privateContact)) {
            privateContacts.remove(code);
            privateContact = null;
        }

        return Optional.ofNullable(privateContact);
    }

    @Override
    public Optional<GroupContact> getGroupContact(long code) {
        GroupContact groupContact = groupContacts.get(code);
        Group group = getXiaomingBot().getMiraiBot().getGroup(code);

        // 没有记录过，就创建新的记录
        if (Objects.isNull(groupContact) && Objects.nonNull(group)) {
            groupContact = new GroupContactImpl(getXiaomingBot(), group);
            groupContacts.put(code, groupContact);
        }

        // 记录过但是本次 get 不到，说明人没了
        if (Objects.isNull(group) && Objects.nonNull(groupContact)) {
            groupContacts.remove(code);
            groupContact = null;
        }

        return Optional.ofNullable(groupContact);
    }

    @Override
    public Optional<MemberContact> getMemberContact(long group, long code) {
        final Optional<GroupContact> optionalGroupContact = getGroupContact(group);
        if (optionalGroupContact.isEmpty()) {
            return Optional.empty();
        }

        final GroupContact groupContact = optionalGroupContact.get();

        final NormalMember member = groupContact.getMiraiContact().get(code);
        return Objects.nonNull(member) ? getMemberContact(groupContact, member) : Optional.empty();
    }

    @Override
    public Optional<MemberContact> getMemberContact(GroupContact groupContact, NormalMember miraiMember) {
        final long code = groupContact.getCode();
        final Map<Long, MemberContact> memberContacts = MapUtil.getOrPutSupply(this.memberContacts, code, ConcurrentHashMap::new);

        final long qq = miraiMember.getId();
        final Container<MemberContact> memberContactContainer = MapUtil.get(memberContacts, qq);

        // 记录了本群，但还没记录这个成员
        if (memberContactContainer.isEmpty()) {
            final MemberContact memberContact = new MemberContactImpl(getGroupContact(code).orElseThrow(), miraiMember);
            memberContactContainer.setValue(memberContact);
            memberContacts.put(qq, memberContact);
        }

        return memberContactContainer.toOptional();
    }

    @Override
    public void onNextMessageEvent(MessageEvent messageEvent) {
        recentMessageEvents.add(messageEvent);
        synchronized (recentMessageConditionalVariable) {
            recentMessageConditionalVariable.notifyAll();
        }
    }

    @Override
    public List<XiaomingContact> getPrivateContactPossibly(long code) {
        final List<XiaomingContact> results = new ArrayList<>();

        // find user in private contact
        getPrivateContact(code).ifPresent(results::add);

        // iterate all groups and get this member
        final List<MemberContact> memberContacts = getXiaomingBot().getMiraiBot()
                .getGroups()
                .stream()
                .map(contact -> new GroupContactImpl(xiaomingBot, contact))
                .map(contact -> contact.getMember(code))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toUnmodifiableList());
        results.addAll(memberContacts);

        return Collections.unmodifiableList(results);
    }
}
