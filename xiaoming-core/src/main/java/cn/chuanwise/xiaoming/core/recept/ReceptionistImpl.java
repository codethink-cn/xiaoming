package cn.chuanwise.xiaoming.core.recept;

import cn.chuanwise.xiaoming.api.recept.GroupReceptionTask;
import cn.chuanwise.xiaoming.api.recept.MemberReceptionTask;
import cn.chuanwise.xiaoming.api.recept.PrivateReceptionTask;
import cn.chuanwise.xiaoming.api.recept.Receptionist;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.api.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.api.contact.message.GroupMessage;
import cn.chuanwise.xiaoming.api.contact.message.Message;
import cn.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.api.contact.message.MemberMessage;
import cn.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import cn.chuanwise.xiaoming.api.user.MemberXiaomingUser;
import cn.chuanwise.xiaoming.core.contact.message.GroupMessageImpl;
import cn.chuanwise.xiaoming.core.contact.message.PrivateMessageImpl;
import cn.chuanwise.xiaoming.core.contact.message.MemberMessageImpl;
import cn.chuanwise.xiaoming.core.object.ModuleObjectImpl;
import cn.chuanwise.xiaoming.core.user.GroupXiaomingUserImpl;
import cn.chuanwise.xiaoming.core.user.MemberXiaomingUserImpl;
import cn.chuanwise.xiaoming.core.user.PrivateXiaomingUserImpl;
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
    final long code;
    final At at;

    public ReceptionistImpl(XiaomingBot xiaomingBot, long code) {
        super(xiaomingBot);
        this.code = code;
        this.at = new At(code);
        this.threadPool = Executors.newFixedThreadPool(xiaomingBot.getConfiguration().getMaxReceptionThreadPoolSize());
    }

    /** 群接待任务 */
    Map<Long, GroupReceptionTask> groupTasks = new ConcurrentHashMap<>();

    /** 群临时会话接待任务 */
    Map<Long, MemberReceptionTask> memberTasks = new ConcurrentHashMap<>();

    /** 私聊接待线程任务 */
    @Setter
    volatile PrivateReceptionTask privateTask;

    /** 最近的群聊消息。String 是 tag */
    Map<String, List<GroupMessage>> groupRecentMessages = new ConcurrentHashMap<>();

    /** 最近私聊消息只存在 ContactManager 中，每次添加消息也是如此。 这里只是存一个引用。 */
    List<PrivateMessage> privateRecentMessages;

    /** 最近临时消息只存在 MemberContact 的 recentMessage 中，每次添加消息也是如此。 这里只是存一个引用。 */
    Map<String, List<MemberMessage>> memberRecentMessages = new ConcurrentHashMap<>();

    /** 全局最新消息 */
    @Setter
    volatile List<? extends Message> globalRecentMessages;

    Map<Long, GroupXiaomingUser> groupXiaomingUsers = new ConcurrentHashMap<>();

    Map<Long, MemberXiaomingUser> memberXiaomingUsers = new ConcurrentHashMap<>();

    PrivateXiaomingUser privateXiaomingUser;

    final ExecutorService threadPool;

    @Override
    public GroupXiaomingUser forGroup(long groupCode) {
        return CollectionUtility.getOrSupplie(groupXiaomingUsers, groupCode,
                () -> new GroupXiaomingUserImpl(getXiaomingBot().getContactManager().getMemberContact(groupCode, code)));
    }

    @Override
    public MemberXiaomingUser forMember(long groupCode) {
        return CollectionUtility.getOrSupplie(memberXiaomingUsers, groupCode,
                () -> new MemberXiaomingUserImpl(getXiaomingBot().getContactManager().getMemberContact(groupCode, code)));
    }

    @Override
    public PrivateXiaomingUser forPrivate() {
        if (Objects.isNull(privateXiaomingUser)) {
            privateXiaomingUser = new PrivateXiaomingUserImpl(getXiaomingBot().getContactManager().getPrivateContact(getCode()));
        }
        return privateXiaomingUser;
    }

    @Override
    public List<GroupMessage> forGroupRecentMessages(String groupTag) {
        return getXiaomingBot().getContactManager().forGroupMemberMessages(groupTag, getCodeString());
    }

    @Override
    public List<MemberMessage> forMemberRecentMessages(String groupTag) {
        return getXiaomingBot().getContactManager().forMemberMessages(groupTag, getCodeString());
    }

    @Override
    public List<PrivateMessage> forPrivateRecentMessages() {
        return getXiaomingBot().getContactManager().forPrivateMessages(getCodeString());
    }

    @Override
    public void onGroupMessage(GroupContact contact, MessageChain messages) {
        GroupMessage message = new GroupMessageImpl(forGroup(contact.getCode()), messages);
        onGroupMessage(contact, message);
    }

    @Override
    public void onGroupMessage(GroupContact contact, String message, MessageChain originalMessageChain) {
        GroupMessage groupMessage = new GroupMessageImpl(forGroup(contact.getCode()), originalMessageChain);
        groupMessage.setMessageChain(MiraiCode.deserializeMiraiCode(message));
        onGroupMessage(contact, groupMessage);
    }

    @Override
    public void onGroupMessage(GroupContact contact, GroupMessage message) {
        final String groupCodeString = contact.getCodeString();
        GroupReceptionTask groupTask = getGroupTask(groupCodeString);
        boolean isFirstRecept = Objects.isNull(groupTask);

        if (isFirstRecept) {
            groupTask = new GroupReceptionTaskImpl(forGroup(contact.getCode()), message);
        }

        groupTask.getUser().onNextInput(message);
        if (isFirstRecept) {
            threadPool.execute(groupTask);
        }
    }

    @Override
    public void onMemberMessage(MemberContact contact, MemberMessage message) {
        MemberReceptionTask memberTask = getMemberTask(contact.getCodeString());

        boolean isFirstRecept = Objects.isNull(memberTask);
        if (isFirstRecept) {
            memberTask = new MemberReceptionTaskImpl(forMember(contact.getCode()), message);
        }

        memberTask.getUser().onNextInput(message);
        if (isFirstRecept) {
            threadPool.execute(memberTask);
        }
    }

    @Override
    public void onMemberMessage(MemberContact contact, MessageChain messages) {
        MemberMessage message = new MemberMessageImpl(forMember(contact.getCode()), messages);
        onMemberMessage(contact, message);
    }

    @Override
    public void onMemberMessage(MemberContact contact, String message, MessageChain originalMessageChain) {
        MemberMessage memberMessage = new MemberMessageImpl(forMember(contact.getCode()), originalMessageChain);
        memberMessage.setMessageChain(MiraiCode.deserializeMiraiCode(message));
        onMemberMessage(contact, memberMessage);
    }

    @Override
    public void onPrivateMessage(PrivateContact contact, PrivateMessage message) {
        boolean isFirstRecept = Objects.isNull(privateTask);

        if (isFirstRecept) {
            privateTask = new PrivateReceptionTaskImpl(forPrivate(), message);
        }

        privateTask.getUser().onNextInput(message);
        if (isFirstRecept) {
            threadPool.execute(privateTask);
        }
    }

    @Override
    public void onPrivateMessage(PrivateContact contact, MessageChain messages) {
        PrivateMessage message = new PrivateMessageImpl(forPrivate(), messages);
        onPrivateMessage(contact, message);
    }

    @Override
    public void onPrivateMessage(PrivateContact contact, String message, MessageChain originalMessageChain) {
        PrivateMessage privateMessage = new PrivateMessageImpl(forPrivate(), originalMessageChain);
        privateMessage.setMessageChain(MiraiCode.deserializeMiraiCode(message));
        onPrivateMessage(contact, privateMessage);
    }
}
