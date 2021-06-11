package com.chuanwise.xiaoming.api.recept;

import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import com.chuanwise.xiaoming.api.contact.contact.TempContact;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import com.chuanwise.xiaoming.api.contact.message.TempMessage;
import com.chuanwise.xiaoming.api.object.ModuleObject;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import com.chuanwise.xiaoming.api.user.TempXiaomingUser;
import com.chuanwise.xiaoming.api.util.InteractorUtils;
import net.mamoe.mirai.message.code.MiraiCode;
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

    ExecutorService getThreadPool();

    long getCode();

    default String getCodeString() {
        return String.valueOf(getCode());
    }

    default void optimize() {
        // 如果大家都是空，就销毁接待员
        if (getGroupTasks().isEmpty() && getTempTasks().isEmpty() && Objects.isNull(getPrivateTask())) {
            stop();
        }
    }

    default boolean isBusy() {
        for (ReceptionTask task : getGroupTasks().values()) {
            if (task.isBusy()) {
                return true;
            }
        }
        for (ReceptionTask task : getTempTasks().values()) {
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
        getTempRecentMessages().values().forEach(notifyer);
        notifyer.accept(getPrivateRecentMessages());

        getThreadPool().shutdown();
        getXiaomingBot().getReceptionistManager().removeReceptionist(getCode());
    }

    default GroupReceptionTask getGroupTask(String tag) {
        return getGroupTasks().get(tag);
    }

    default TempReceptionTask getTempTask(String tag)  {
        return getTempTasks().get(tag);
    }

    Map<String, GroupReceptionTask> getGroupTasks();

    default GroupMessage nextGroupMessage(String tag, long timeout) {
        return InteractorUtils.waitLastElement(getOrPutGroupRecentMessage(tag), timeout);
    }

    Map<String, TempReceptionTask> getTempTasks();

    default TempMessage nextTempMessage(String tag, long timeout) {
        return InteractorUtils.waitLastElement(getOrPutTempRecentMessage(tag), timeout);
    }

    PrivateReceptionTask getPrivateTask();

    default PrivateMessage nextPrivateMessage(long timeout) {
        return InteractorUtils.waitLastElement(getPrivateRecentMessages(), timeout);
    }

    void setPrivateTask(PrivateReceptionTask task);

    void onGroupMessage(GroupContact contact, String message, MessageChain originalMessageChain);

    void onGroupMessage(GroupContact contact, MessageChain messages);

    void onGroupMessage(GroupContact contact, GroupMessage message);

    void onTempMessage(TempContact contact, String message, MessageChain originalMessageChain);

    void onTempMessage(TempContact contact, MessageChain messages);

    void onTempMessage(TempContact contact, TempMessage message);

    void onPrivateMessage(PrivateContact contact, String message, MessageChain originalMessageChain);

    void onPrivateMessage(PrivateContact contact, MessageChain messages);

    void onPrivateMessage(PrivateContact contact, PrivateMessage message);

    List<? extends Message> getGlobalRecentMessages();

    Map<String, List<TempMessage>> getTempRecentMessages();

    Map<String, List<GroupMessage>> getGroupRecentMessages();

    List<PrivateMessage> getPrivateRecentMessages();

    Map<Long, GroupXiaomingUser> getGroupXiaomingUsers();

    Map<Long, TempXiaomingUser> getTempXiaomingUsers();

    PrivateXiaomingUser getPrivateXiaomingUser();

    PrivateXiaomingUser getOrPutPrivateXiaomingUser(PrivateContact contact);

    default GroupXiaomingUser getGroupXiaomingUser(long code) {
        return getGroupXiaomingUsers().get(code);
    }

    GroupXiaomingUser getOrPutGroupXiaomingUser(GroupContact groupContact, TempContact tempContact);

    default TempXiaomingUser getTempXiaomingUser(long code) {
        return getTempXiaomingUsers().get(code);
    }

    TempXiaomingUser getOrPutTempXiaomingUser(TempContact contact);

    void setGlobalRecentMessages(List<? extends Message> list);

    default List<GroupMessage> getGroupRecentMessage(String tag) {
        return getGroupRecentMessages().get(tag);
    }

    default List<GroupMessage> getOrPutGroupRecentMessage(String tag) {
        List<GroupMessage> recentMessage = getGroupRecentMessage(tag);
        if (Objects.isNull(recentMessage)) {
            recentMessage = new LinkedList<>();
            getGroupRecentMessages().put(tag, recentMessage);
        }
        return recentMessage;
    }

    default List<TempMessage> getTempRecentMessage(String tag) {
        return getTempRecentMessages().get(tag);
    }

    default List<TempMessage> getOrPutTempRecentMessage(String tag) {
        List<TempMessage> recentMessage = getTempRecentMessage(tag);
        if (Objects.isNull(recentMessage)) {
            recentMessage = new LinkedList<>();
            getTempRecentMessages().put(tag, recentMessage);
        }
        return recentMessage;
    }
}