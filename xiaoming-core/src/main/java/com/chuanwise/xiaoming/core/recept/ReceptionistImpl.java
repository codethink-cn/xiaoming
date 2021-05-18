package com.chuanwise.xiaoming.core.recept;

import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.user.Receptionist;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Member;

import java.util.Map;
import java.util.Objects;
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
    final ExecutorService threadPool;

    public ReceptionistImpl(XiaomingUser user) {
        super(user.getXiaomingBot());
        this.user = user;
        user.setReceptionist(this);
        threadPool = Executors.newFixedThreadPool(getXiaomingBot().getConfiguration().getMaxReceptThreadNumber());
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
    ReceptionTask privateTask, externalTask;

    @Override
    public void removePrivateTask() {
        privateTask = null;
    }

    @Override
    public void removeExternalTask() {
        externalTask = null;
    }

    @Override
    public ReceptionTask getOrPutExternalTask(Member member) {
        ReceptionTask externalTask = getExternalTask();
        if (Objects.isNull(externalTask)) {
            externalTask = ReceptionTaskImpl.externalTask(this, member);
            this.externalTask = externalTask;
            getThreadPool().execute(externalTask);
        }
        return externalTask;
    }

    @Override
    public ReceptionTask getOrPutGroupTask(ResponseGroup responseGroup, Member member) {
        final long group = responseGroup.getCode();
        ReceptionTask groupTask = getGroupTask(group);
        if (Objects.isNull(groupTask)) {
            groupTask = ReceptionTaskImpl.groupTask(this, member);
            groupTasks.put(group, groupTask);
            getThreadPool().execute(groupTask);
        }
        return groupTask;
    }

    @Override
    public ReceptionTask getOrPutTempTask(ResponseGroup responseGroup, Member member) {
        final long group = responseGroup.getCode();
        ReceptionTask tempTask = getTempTask(group);
        if (Objects.isNull(tempTask)) {
            tempTask = ReceptionTaskImpl.tempTask(this, member);
            tempTasks.put(group, tempTask);
            getThreadPool().execute(tempTask);
        }
        return tempTask;
    }

    @Override
    public ReceptionTask getOrPutPrivateTask(Friend friend) {
        if (Objects.isNull(getPrivateTask())) {
            privateTask = ReceptionTaskImpl.privateTask(this, friend);
            getThreadPool().execute(privateTask);
        }
        return privateTask;
    }
}
