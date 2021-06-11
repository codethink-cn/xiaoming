package com.chuanwise.xiaoming.core.recept;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import com.chuanwise.xiaoming.api.contact.contact.TempContact;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import com.chuanwise.xiaoming.api.contact.message.TempMessage;
import com.chuanwise.xiaoming.api.recept.*;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import com.chuanwise.xiaoming.api.user.TempXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.contact.message.GroupMessageImpl;
import com.chuanwise.xiaoming.core.contact.message.PrivateMessageImpl;
import com.chuanwise.xiaoming.core.contact.message.TempMessageImpl;
import com.chuanwise.xiaoming.core.object.ModuleObjectImpl;
import com.chuanwise.xiaoming.core.user.GroupXiaomingUserImpl;
import com.chuanwise.xiaoming.core.user.PrivateXiaomingUserImpl;
import com.chuanwise.xiaoming.core.user.TempXiaomingUserImpl;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    Map<String, TempReceptionTask> tempTasks = new ConcurrentHashMap<>();

    /**
     * 私聊接待线程
     */
    @Setter
    PrivateReceptionTask privateTask;

    /** 最近的群聊消息。String 是 tag */
    Map<String, List<GroupMessage>> groupRecentMessages = new HashMap<>();

    List<PrivateMessage> privateRecentMessages = new LinkedList<>();

    Map<String, List<TempMessage>> tempRecentMessages = new HashMap<>();

    @Setter
    List<? extends Message> globalRecentMessages = new LinkedList<>();

    Map<Long, GroupXiaomingUser> groupXiaomingUsers = new HashMap<>();

    Map<Long, TempXiaomingUser> tempXiaomingUsers = new HashMap<>();

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
    public GroupXiaomingUser getOrPutGroupXiaomingUser(GroupContact groupContact, TempContact tempContact) {
        final long groupCode = groupContact.getCode();
        GroupXiaomingUser groupXiaomingUser = getGroupXiaomingUser(groupCode);
        if (Objects.isNull(groupXiaomingUser)) {
            groupXiaomingUser = new GroupXiaomingUserImpl(groupContact, tempContact, getOrPutGroupRecentMessage(groupContact.getCodeString()));
            groupXiaomingUser.setReceptionist(this);
            groupXiaomingUsers.put(groupCode, groupXiaomingUser);
        }
        return groupXiaomingUser;
    }

    @Override
    public TempXiaomingUser getOrPutTempXiaomingUser(TempContact contact) {
        final long groupCode = contact.getCode();
        TempXiaomingUser tempXiaomingUser = getTempXiaomingUser(groupCode);
        if (Objects.isNull(tempXiaomingUser)) {
            tempXiaomingUser = new TempXiaomingUserImpl(contact, getOrPutTempRecentMessage(contact.getCodeString()));
            tempXiaomingUser.setReceptionist(this);
            tempXiaomingUsers.put(groupCode, tempXiaomingUser);
        }
        return tempXiaomingUser;
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
                final List<GroupMessage> groupRecentMessage = getOrPutGroupRecentMessage(tag);
                groupRecentMessage.add(message);
                synchronized (groupRecentMessage) {
                    groupRecentMessage.notifyAll();
                }
            }

            groupTask = new GroupReceptionTaskImpl(getOrPutGroupXiaomingUser(contact, message.getSender().getTempContact()), getOrPutGroupRecentMessage(contact.getCodeString()));
        }
        final List<GroupMessage> list = groupTask.getUser().getRecentMessages();
        list.add(message);
        setGlobalRecentMessages(list);
        if (isFirstRecept) {
            threadPool.execute(groupTask);
        } else {
            synchronized (list) {
                list.notifyAll();
            }
        }

        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    public void onTempMessage(TempContact contact, TempMessage message) {
        contact.addRecentMessage(message);
        TempReceptionTask tempTask = getTempTask(contact.getCodeString());
        boolean isFirstRecept = Objects.isNull(tempTask);
        if (isFirstRecept) {
            // 把消息送到每一个 tag 表里
            for (String tag : contact.getGroupContact().getTags()) {
                final List<TempMessage> tempRecentMessage = getOrPutTempRecentMessage(tag);
                tempRecentMessage.add(message);
                synchronized (tempRecentMessage) {
                    tempRecentMessage.notifyAll();
                }
            }

            tempTask = new TempReceptionTaskImpl(getOrPutTempXiaomingUser(contact), getOrPutTempRecentMessage(contact.getGroupContact().getCodeString()));
        }
        final List<TempMessage> list = tempTask.getUser().getRecentMessages();
        list.add(message);
        setGlobalRecentMessages(list);
        if (isFirstRecept) {
            threadPool.execute(tempTask);
        } else {
            synchronized (list) {
                list.notifyAll();
            }
        }

        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    public void onTempMessage(TempContact contact, MessageChain messages) {
        TempMessage message = new TempMessageImpl(getOrPutTempXiaomingUser(contact), messages);
        onTempMessage(contact, message);
    }

    @Override
    public void onTempMessage(TempContact contact, String message, MessageChain originalMessageChain) {
        TempMessage tempMessage = new TempMessageImpl(getOrPutTempXiaomingUser(contact), originalMessageChain);
        tempMessage.setMessageChain(MiraiCode.deserializeMiraiCode(message));
        onTempMessage(contact, tempMessage);
    }

    @Override
    public void onPrivateMessage(PrivateContact contact, PrivateMessage message) {
        contact.addRecentMessage(message);
        boolean isFirstRecept = Objects.isNull(privateTask);
        if (isFirstRecept) {
            privateRecentMessages.add(message);
            privateTask = new PrivateReceptionTaskImpl(getOrPutPrivateXiaomingUser(contact), privateRecentMessages);
        }
        final List<PrivateMessage> list = this.privateRecentMessages;
        setGlobalRecentMessages(list);
        if (isFirstRecept) {
            threadPool.execute(privateTask);
        } else {
            synchronized (list) {
                list.notifyAll();
            }
        }

        synchronized (this) {
            this.notifyAll();
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
