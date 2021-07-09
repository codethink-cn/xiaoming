package com.chuanwise.xiaoming.api.recept;

import com.chuanwise.utility.CollectionUtility;
import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import com.chuanwise.xiaoming.api.contact.contact.MemberContact;
import com.chuanwise.xiaoming.api.contact.message.*;
import com.chuanwise.xiaoming.api.object.ModuleObject;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import com.chuanwise.xiaoming.api.user.MemberXiaomingUser;
import com.chuanwise.xiaoming.api.util.InteractorUtils;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        Consumer<Object> notifyer = messages -> {
            synchronized (messages) {
                messages.notifyAll();
            }
        };
        notifyer.accept(this);

        getGroupRecentMessages().values().forEach(notifyer);
        getMemberRecentMessages().values().forEach(notifyer);
        if (Objects.nonNull(getPrivateTask())) {
            notifyer.accept(getPrivateTask().getRecentMessages());
        }

        getThreadPool().shutdown();

        getXiaomingBot().getReceptionistManager().removeReceptionist(getCode());
    }

    ExecutorService getThreadPool();

    default GroupReceptionTask getGroupTask(String tag) {
        return getGroupTasks().get(tag);
    }

    default MemberReceptionTask getMemberTask(String tag)  {
        return getMemberTasks().get(tag);
    }

    Map<Long, GroupReceptionTask> getGroupTasks();

    Map<Long, MemberReceptionTask> getMemberTasks();

    PrivateReceptionTask getPrivateTask();

    default PrivateMessage nextPrivateMessage(long timeout) {
        return InteractorUtils.waitLastElement(forPrivateRecentMessages(), timeout);
    }

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

    Map<Long, GroupXiaomingUser> getGroupXiaomingUsers();

    Map<Long, MemberXiaomingUser> getMemberXiaomingUsers();

    PrivateXiaomingUser forPrivate();

    GroupXiaomingUser forGroup(long groupCode);

    MemberXiaomingUser forMember(long code);

    List<PrivateMessage> forPrivateRecentMessages();

    default List<GroupMessage> forGroupRecentMessages(String groupTag) {
        return CollectionUtility.getOrSupplie(getGroupRecentMessages(), groupTag, LinkedList::new);
    }

    default List<MemberMessage> forMemberRecentMessages(String groupTag) {
        return CollectionUtility.getOrSupplie(getMemberRecentMessages(), groupTag, LinkedList::new);
    }

    void setGlobalRecentMessages(List<? extends Message> list);

    void setPrivateTask(PrivateReceptionTask privateReceptionTask);
}