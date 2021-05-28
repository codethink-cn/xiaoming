package com.chuanwise.xiaoming.core.recept;

import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 小明接待员，实际上是主管和一个用户交互的线程
 * @author Chuanwise
 */
@Getter
public class ReceptionistImpl extends HostObjectImpl implements Receptionist {
    final XiaomingUser user;
    final ExecutorService threadPool = Executors.newCachedThreadPool();

    public ReceptionistImpl(XiaomingUser user) {
        super(user.getXiaomingBot());
        this.user = user;
        user.setReceptionist(this);
    }

    /**
     * 群接待线程
     */
    Map<Long, ReceptionTask> groupTasks = new ConcurrentHashMap<>();

    /**
     * 群临时会话接待线程
     */
    Map<Long, ReceptionTask> tempTasks = new ConcurrentHashMap<>();

    /**
     * 私聊接待线程
     */
    @Setter
    ReceptionTask privateTask;

    Map<String, ReceptionTask> receptionTasks = new ConcurrentHashMap<>();

    @Override
    public void onGroupMessage(Member member, String message) {
        final Group group = member.getGroup();

        final ReceptionTask groupTask = getGroupTask(group.getId());
        if (Objects.nonNull(groupTask)) {
            groupTask.onMessage(message);
        } else {
            final List<String> messages = getUser().getOrPutRecentGroupMessages(group.getId());
            messages.add(message.trim());
            getUser().setGlobalNextMessage(messages);
            threadPool.execute(ReceptionTaskImpl.groupTask(this, member));
        }
        final Set<Thread> globalMessageWaiter = getUser().getGlobalMessageWaiter();
        synchronized (globalMessageWaiter) {
            globalMessageWaiter.notifyAll();
        }
    }

    @Override
    public void onTempMessage(Member member, String message) {
        final Group group = member.getGroup();

        final ReceptionTask tempTask = getTempTask(group.getId());
        if (Objects.nonNull(tempTask)) {
            tempTask.onMessage(message);
        } else {
            final List<String> messages = getUser().getOrPutRecentTempMessages(group.getId());
            messages.add(message.trim());
            getUser().setGlobalNextMessage(messages);
            threadPool.execute(ReceptionTaskImpl.tempTask(this, member));
        }
        final Set<Thread> globalMessageWaiter = getUser().getGlobalMessageWaiter();
        synchronized (globalMessageWaiter) {
            globalMessageWaiter.notifyAll();
        }
    }

    @Override
    public void onPrivateMessage(Friend friend, String message) {
        if (Objects.nonNull(privateTask)) {
            privateTask.onMessage(message);
        } else {
            final List<String> messages = getUser().getRecentPrivateMessage();
            messages.add(message.trim());
            getUser().setGlobalNextMessage(messages);
            threadPool.execute(ReceptionTaskImpl.privateTask(this, friend));
        }
        final Set<Thread> globalMessageWaiter = getUser().getGlobalMessageWaiter();
        synchronized (globalMessageWaiter) {
            globalMessageWaiter.notifyAll();
        }
    }
}
