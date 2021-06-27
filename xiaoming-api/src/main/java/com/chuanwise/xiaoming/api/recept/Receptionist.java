package com.chuanwise.xiaoming.api.recept;

import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import com.chuanwise.xiaoming.api.contact.contact.MemberContact;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import com.chuanwise.xiaoming.api.contact.message.MemberMessage;
import com.chuanwise.xiaoming.api.object.ModuleObject;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import com.chuanwise.xiaoming.api.user.MemberXiaomingUser;
import com.chuanwise.xiaoming.api.util.InteractorUtils;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * 小明接待员
 */
public interface Receptionist extends ModuleObject {
    At getAt();

    long getCode();

    default String getCodeString() {
        return String.valueOf(getCode());
    }

    default void optimize() {
        // 如果大家都是空，就销毁接待员
        if (getGroupTasks().isEmpty() && getMemberTasks().isEmpty() && Objects.isNull(getPrivateTask())) {
            stop();
        }
    }

    default boolean isBusy() {
        for (ReceptionTask task : getGroupTasks().values()) {
            if (task.isBusy()) {
                return true;
            }
        }
        for (ReceptionTask task : getMemberTasks().values()) {
            if (task.isBusy()) {
                return true;
            }
        }
        return Objects.nonNull(getPrivateTask()) && getPrivateTask().isBusy();
    }

    default void stop() {
        Consumer<List<? extends Message>> notifyer = messages -> {
            synchronized (messages) {
                messages.notifyAll();
            }
        };
        synchronized (this) {
            notifyAll();
        }

        getGroupRecentMessages().values().forEach(notifyer);
        getMemberRecentMessages().values().forEach(notifyer);
        notifyer.accept(getPrivateRecentMessages());

        getXiaomingBot().getReceptionistManager().removeReceptionist(getCode());
    }

    default GroupReceptionTask getGroupTask(String tag) {
        return getGroupTasks().get(tag);
    }

    default MemberReceptionTask getMemberTask(String tag)  {
        return getMemberTasks().get(tag);
    }

    Map<String, GroupReceptionTask> getGroupTasks();

    default GroupMessage nextGroupMessage(String tag, long timeout) {
        return InteractorUtils.waitLastElement(getOrPutGroupRecentMessages(tag), timeout);
    }

    Map<String, MemberReceptionTask> getMemberTasks();

    default MemberMessage nextMemberMessage(String tag, long timeout) {
        return InteractorUtils.waitLastElement(getOrPutMemberRecentMessages(tag), timeout);
    }

    PrivateReceptionTask getPrivateTask();

    default PrivateMessage nextPrivateMessage(long timeout) {
        return InteractorUtils.waitLastElement(getPrivateRecentMessages(), timeout);
    }

    void setPrivateTask(PrivateReceptionTask task);

    void onGroupMessage(GroupContact contact, String message, MessageChain originalMessageChain);

    void onGroupMessage(GroupContact contact, MessageChain messages);

    void onGroupMessage(GroupContact contact, GroupMessage message);

    void onMemberMessage(MemberContact contact, String message, MessageChain originalMessageChain);

    void onMemberMessage(MemberContact contact, MessageChain messages);

    void onMemberMessage(MemberContact contact, MemberMessage message);

    void onPrivateMessage(PrivateContact contact, String message, MessageChain originalMessageChain);

    void onPrivateMessage(PrivateContact contact, MessageChain messages);

    void onPrivateMessage(PrivateContact contact, PrivateMessage message);

    List<? extends Message> getGlobalRecentMessages();

    Map<String, List<MemberMessage>> getMemberRecentMessages();

    Map<String, List<GroupMessage>> getGroupRecentMessages();

    List<PrivateMessage> getPrivateRecentMessages();

    Map<Long, GroupXiaomingUser> getGroupXiaomingUsers();

    Map<Long, MemberXiaomingUser> getMemberXiaomingUsers();

    PrivateXiaomingUser getPrivateXiaomingUser();

    PrivateXiaomingUser getOrPutPrivateXiaomingUser(PrivateContact contact);

    default GroupXiaomingUser getGroupXiaomingUser(long code) {
        return getGroupXiaomingUsers().get(code);
    }

    GroupXiaomingUser getOrPutGroupXiaomingUser(GroupContact groupContact, MemberContact memberContact);

    default MemberXiaomingUser getMemberXiaomingUser(long code) {
        return getMemberXiaomingUsers().get(code);
    }

    MemberXiaomingUser getOrPutMemberXiaomingUser(MemberContact contact);

    void setGlobalRecentMessages(List<? extends Message> list);

    default List<GroupMessage> getGroupRecentMessages(String tag) {
        return getGroupRecentMessages().get(tag);
    }

    default List<GroupMessage> getOrPutGroupRecentMessages(String tag) {
        List<GroupMessage> recentMessage = getGroupRecentMessages(tag);
        if (Objects.isNull(recentMessage)) {
            recentMessage = new CopyOnWriteArrayList<>();
            getGroupRecentMessages().put(tag, recentMessage);
        }
        return recentMessage;
    }

    default List<MemberMessage> getMemberRecentMessages(String tag) {
        return getMemberRecentMessages().get(tag);
    }

    default List<MemberMessage> getOrPutMemberRecentMessages(String tag) {
        List<MemberMessage> recentMessage = getMemberRecentMessages(tag);
        if (Objects.isNull(recentMessage)) {
            recentMessage = new CopyOnWriteArrayList<>();
            getMemberRecentMessages().put(tag, recentMessage);
        }
        return recentMessage;
    }
}