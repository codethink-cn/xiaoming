package com.chuanwise.xiaoming.core.recept;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import com.chuanwise.xiaoming.api.contact.contact.MemberContact;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import com.chuanwise.xiaoming.api.contact.message.MemberMessage;
import com.chuanwise.xiaoming.api.recept.*;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import com.chuanwise.xiaoming.api.user.MemberXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.contact.message.GroupMessageImpl;
import com.chuanwise.xiaoming.core.contact.message.PrivateMessageImpl;
import com.chuanwise.xiaoming.core.contact.message.MemberMessageImpl;
import com.chuanwise.xiaoming.core.object.ModuleObjectImpl;
import com.chuanwise.xiaoming.core.user.GroupXiaomingUserImpl;
import com.chuanwise.xiaoming.core.user.PrivateXiaomingUserImpl;
import com.chuanwise.xiaoming.core.user.MemberXiaomingUserImpl;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 小明接待员
 * @author Chuanwise
 */
@Getter
public class ReceptionistImpl extends ModuleObjectImpl implements Receptionist {
    final ExecutorService threadPool = Executors.newCachedThreadPool();
    final long code;
    final At at;

    public ReceptionistImpl(XiaomingBot xiaomingBot, long code) {
        super(xiaomingBot);
        this.code = code;
        this.at = new At(code);
    }

    public ReceptionistImpl(XiaomingUser user) {
        this(user.getXiaomingBot(), user.getCode());
    }

    /**
     * 群接待线程
     */
    Map<String, GroupReceptionTask> groupTasks = new ConcurrentHashMap<>();

    /**
     * 群临时会话接待线程
     */
    Map<String, MemberReceptionTask> memberTasks = new ConcurrentHashMap<>();

    /**
     * 私聊接待线程
     */
    @Setter
    PrivateReceptionTask privateTask;

    /** 最近的群聊消息。String 是 tag */
    Map<String, List<GroupMessage>> groupRecentMessages = new ConcurrentHashMap<>();

    List<PrivateMessage> privateRecentMessages = new CopyOnWriteArrayList<>();

    Map<String, List<MemberMessage>> memberRecentMessages = new ConcurrentHashMap<>();

    @Setter
    List<? extends Message> globalRecentMessages;

    Map<Long, GroupXiaomingUser> groupXiaomingUsers = new ConcurrentHashMap<>();

    Map<Long, MemberXiaomingUser> memberXiaomingUsers = new ConcurrentHashMap<>();

    PrivateXiaomingUser privateXiaomingUser;

    @Override
    public PrivateXiaomingUser getOrPutPrivateXiaomingUser(PrivateContact contact) {
        if (Objects.isNull(privateXiaomingUser)) {
            privateXiaomingUser = new PrivateXiaomingUserImpl(contact, privateRecentMessages);
            privateXiaomingUser.setReceptionist(this);
        }
        return privateXiaomingUser;
    }

    @Override
    public GroupXiaomingUser getOrPutGroupXiaomingUser(GroupContact groupContact, MemberContact memberContact) {
        final long groupCode = groupContact.getCode();
        GroupXiaomingUser groupXiaomingUser = getGroupXiaomingUser(groupCode);
        if (Objects.isNull(groupXiaomingUser)) {
            groupXiaomingUser = new GroupXiaomingUserImpl(groupContact, memberContact, getOrPutGroupRecentMessages(groupContact.getCodeString()));
            groupXiaomingUser.setReceptionist(this);
            synchronized (groupXiaomingUsers) {
                groupXiaomingUsers.put(groupCode, groupXiaomingUser);
            }
        }
        return groupXiaomingUser;
    }

    @Override
    public MemberXiaomingUser getOrPutMemberXiaomingUser(MemberContact contact) {
        final long groupCode = contact.getCode();
        MemberXiaomingUser memberXiaomingUser = getMemberXiaomingUser(groupCode);
        if (Objects.isNull(memberXiaomingUser)) {
            memberXiaomingUser = new MemberXiaomingUserImpl(contact, getOrPutMemberRecentMessages(contact.getCodeString()));
            memberXiaomingUser.setReceptionist(this);
            synchronized (memberXiaomingUsers) {
                memberXiaomingUsers.put(groupCode, memberXiaomingUser);
            }
        }
        return memberXiaomingUser;
    }

    @Override
    public void onGroupMessage(GroupContact contact, MessageChain messages) {
        GroupMessage message = new GroupMessageImpl(getOrPutGroupXiaomingUser(contact, contact.getMember(code)), messages);
        onGroupMessage(contact, message);
    }

    @Override
    public void onGroupMessage(GroupContact contact, String message, MessageChain originalMessageChain) {
        GroupMessage groupMessage = new GroupMessageImpl(getOrPutGroupXiaomingUser(contact, contact.getMember(code)), originalMessageChain);
        groupMessage.setMessageChain(MiraiCode.deserializeMiraiCode(message));
        onGroupMessage(contact, groupMessage);
    }

    @Override
    public void onGroupMessage(GroupContact contact, GroupMessage message) {
        contact.addRecentMessage(message);
        GroupReceptionTask groupTask = getGroupTask(contact.getCodeString());
        boolean isFirstRecept = Objects.isNull(groupTask);
        if (isFirstRecept) {
            // 把消息送到每一个 tag 表里
            for (String tag : contact.getTags()) {
                final List<GroupMessage> groupRecentMessage = getOrPutGroupRecentMessages(tag);
                synchronized (groupRecentMessage) {
                    groupRecentMessage.add(message);
                }
                synchronized (groupRecentMessage) {
                    groupRecentMessage.notifyAll();
                }
            }

            groupTask = new GroupReceptionTaskImpl(getOrPutGroupXiaomingUser(contact, message.getSender().getMemberContact()), getOrPutGroupRecentMessages(contact.getCodeString()));
        }

        groupTask.getUser().onNextInput(message);
        if (isFirstRecept) {
            threadPool.execute(groupTask);
        }
    }

    @Override
    public void onMemberMessage(MemberContact contact, MemberMessage message) {
        contact.addRecentMessage(message);
        MemberReceptionTask memberTask = getMemberTask(contact.getCodeString());
        boolean isFirstRecept = Objects.isNull(memberTask);
        if (isFirstRecept) {
            // 把消息送到每一个 tag 表里
            for (String tag : contact.getGroupContact().getTags()) {
                final List<MemberMessage> memberRecentMessage = getOrPutMemberRecentMessages(tag);
                synchronized (memberRecentMessage) {
                    memberRecentMessage.add(message);
                }
                synchronized (memberRecentMessage) {
                    memberRecentMessage.notifyAll();
                }
            }

            memberTask = new MemberReceptionTaskImpl(getOrPutMemberXiaomingUser(contact), getOrPutMemberRecentMessages(contact.getGroupContact().getCodeString()));
        }

        memberTask.getUser().onNextInput(message);
        if (isFirstRecept) {
            threadPool.execute(memberTask);
        }
    }

    @Override
    public void onMemberMessage(MemberContact contact, MessageChain messages) {
        MemberMessage message = new MemberMessageImpl(getOrPutMemberXiaomingUser(contact), messages);
        onMemberMessage(contact, message);
    }

    @Override
    public void onMemberMessage(MemberContact contact, String message, MessageChain originalMessageChain) {
        MemberMessage memberMessage = new MemberMessageImpl(getOrPutMemberXiaomingUser(contact), originalMessageChain);
        memberMessage.setMessageChain(MiraiCode.deserializeMiraiCode(message));
        onMemberMessage(contact, memberMessage);
    }

    @Override
    public void onPrivateMessage(PrivateContact contact, PrivateMessage message) {
        contact.addRecentMessage(message);
        boolean isFirstRecept = Objects.isNull(privateTask);
        if (isFirstRecept) {
            synchronized (privateRecentMessages) {
                privateRecentMessages.add(message);
            }
            privateTask = new PrivateReceptionTaskImpl(getOrPutPrivateXiaomingUser(contact), privateRecentMessages);
        }

        privateTask.getUser().onNextInput(message);
        if (isFirstRecept) {
            threadPool.execute(privateTask);
        }
    }

    @Override
    public void onPrivateMessage(PrivateContact contact, MessageChain messages) {
        PrivateMessage message = new PrivateMessageImpl(getOrPutPrivateXiaomingUser(contact), messages);
        onPrivateMessage(contact, message);
    }

    @Override
    public void onPrivateMessage(PrivateContact contact, String message, MessageChain originalMessageChain) {
        PrivateMessage privateMessage = new PrivateMessageImpl(getOrPutPrivateXiaomingUser(contact), originalMessageChain);
        privateMessage.setMessageChain(MiraiCode.deserializeMiraiCode(message));
        onPrivateMessage(contact, privateMessage);
    }
}
