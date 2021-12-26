package cn.chuanwise.xiaoming.contact;

import cn.chuanwise.exception.UnsupportedVersionException;
import cn.chuanwise.toolkit.box.Box;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class ContactManagerImpl extends ModuleObjectImpl implements ContactManager {
    final List<MessageEvent> recentMessageEvents;

    final List<SendMessageEvent> recentSentMessageEvents;

    final List<SendMessageEvent> sendMessageList = new CopyOnWriteArrayList<>();

    final Box<Thread> sendMessageLoop = Box.empty();
    final PrivateContact botPrivateContact;

    public ContactManagerImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
        recentMessageEvents = Collections.synchronizedList(new SizedCopyOnWriteArrayList<>(xiaomingBot.getConfiguration().getMaxRecentMessageBufferSize()));
        recentSentMessageEvents = Collections.synchronizedList(new SizedCopyOnWriteArrayList<>(xiaomingBot.getConfiguration().getMaxRecentMessageBufferSize()));

        botPrivateContact = new PrivateContactImpl(xiaomingBot, xiaomingBot.getMiraiBot().getAsFriend());
    }

    @Override
    public Future<Optional<Message>> readyToSend(SendMessageEvent event) {
        recentSentMessageEvents.add(event);
        sendMessageList.add(event);

        sendMessageLoop.ifEmpty(() -> getXiaomingBot().getScheduler().run(() -> {
            try {
                sendMessageLoop.set(Thread.currentThread());

                while (!sendMessageList.isEmpty()) {
                    final SendMessageEvent cursor = sendMessageList.remove(0);

                    if (!cursor.isCancelled()) {
                        final XiaomingContact contact = cursor.getTarget();
                        final Contact miraiContact = contact.getMiraiContact();

                        final MessageReceipt messageReceipt = miraiContact.sendMessage(cursor.getMessageChain());
                        final OnlineMessageSource.Outgoing source = messageReceipt.getSource();

                        cursor.getMessageBox().set(new MessageImpl(xiaomingBot, source.getOriginalMessage(), source.getTime()));
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
            final Box<Message> messageBox = event.getMessageBox();
            if (messageBox.isPresent()) {
                return messageBox.toOptional();
            } else {
                return Optional.ofNullable(messageBox.nextValue());
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
        if (ObjectUtil.waitUtil(recentMessageConditionalVariable, timeout, () -> filter.test(recentMessageEvents.get(recentMessageEvents.size() - 1)))) {
            return Optional.of(recentMessageEvents.get(recentMessageEvents.size() - 1));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<PrivateContact> getPrivateContact(long code) {
        return Optional.ofNullable(getXiaomingBot().getMiraiBot().getFriend(code))
                .map(contact -> new PrivateContactImpl(xiaomingBot, contact));
    }

    @Override
    public Optional<GroupContact> getGroupContact(long code) {
        return Optional.ofNullable(getXiaomingBot().getMiraiBot().getGroup(code))
                .map(contact -> new GroupContactImpl(xiaomingBot, contact));
    }

    @Override
    public Optional<MemberContact> getMemberContact(long groupCode, long accountCode) {
        final Optional<GroupContact> optionalGroupContact = getGroupContact(groupCode);
        if (optionalGroupContact.isEmpty()) {
            return Optional.empty();
        }
        final GroupContact groupContact = optionalGroupContact.get();


        return Optional.ofNullable(groupContact.getMiraiContact().get(accountCode))
                .map(x -> new MemberContactImpl(groupContact, x));
    }

    @Override
    public void onNextMessageEvent(MessageEvent messageEvent) {
        recentMessageEvents.add(messageEvent);
        synchronized (recentMessageConditionalVariable) {
            recentMessageConditionalVariable.notifyAll();
        }
    }

    @Override
    public List<GroupContact> getGroupContacts() {
        return xiaomingBot.getMiraiBot()
                .getGroups()
                .stream()
                .map(contact -> new GroupContactImpl(xiaomingBot, contact))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<PrivateContact> getPrivateContacts() {
        return xiaomingBot.getMiraiBot()
                .getFriends()
                .stream()
                .map(contact -> new PrivateContactImpl(xiaomingBot, contact))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<XiaomingContact> getPrivateContactPossibly(long code) {
        final List<XiaomingContact> results = new ArrayList<>();

        // find user in private contact
        getPrivateContact(code).ifPresent(results::add);

        // iterate all groups and get this member
        final List<MemberContact> memberContacts = getGroupContacts()
                .stream()
                .map(contact -> contact.getMember(code))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toUnmodifiableList());
        results.addAll(memberContacts);

        return Collections.unmodifiableList(results);
    }
}
