package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.contact.message.GroupMessage;
import cn.chuanwise.xiaoming.contact.message.MemberMessage;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.attribute.AttributeHolder;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.PrivateXiaomingUser;
import cn.chuanwise.xiaoming.user.MemberXiaomingUser;
import cn.chuanwise.xiaoming.utility.InteractorUtility;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * 小明接待员
 */
public interface Receptionist extends ModuleObject, AttributeHolder {
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
        return InteractorUtility.waitLastElement(forPrivateRecentMessages(), timeout);
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

    Map<Long, GroupXiaomingUser> getGroupXiaomingUsers();

    Map<Long, MemberXiaomingUser> getMemberXiaomingUsers();

    PrivateXiaomingUser forPrivate();

    GroupXiaomingUser forGroup(long groupCode);

    MemberXiaomingUser forMember(long code);

    List<PrivateMessage> forPrivateRecentMessages();

    default List<GroupMessage> forGroupRecentMessages(String groupTag) {
        return getXiaomingBot().getContactManager().forGroupMemberMessages(groupTag, getCodeString());
    }

    default List<MemberMessage> forMemberRecentMessages(String groupTag) {
        return getXiaomingBot().getContactManager().forMemberMessages(groupTag, getCodeString());
    }

    void setGlobalRecentMessages(List<? extends Message> list);

    void setPrivateTask(PrivateReceptionTask privateReceptionTask);
}