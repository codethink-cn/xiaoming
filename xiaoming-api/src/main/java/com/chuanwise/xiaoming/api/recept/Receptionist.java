package com.chuanwise.xiaoming.api.recept;

import com.chuanwise.xiaoming.api.object.HostObject;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * 小明接待员
 */
public interface Receptionist extends HostObject {
    ExecutorService getThreadPool();

    XiaomingUser getUser();

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
        getGroupTasks().values().forEach(ReceptionTask::stop);
        getTempTasks().values().forEach(ReceptionTask::stop);
        if (Objects.nonNull(getPrivateTask())) {
            getPrivateTask().stop();
        }
        getThreadPool().shutdown();
        getXiaomingBot().getReceptionistManager().removeReceptionist(getUser().getQQ());
    }

    default void forceStop() {
        getGroupTasks().values().forEach(ReceptionTask::forceStop);
        getTempTasks().values().forEach(ReceptionTask::forceStop);
        if (Objects.nonNull(getPrivateTask())) {
            getPrivateTask().forceStop();
        }
        getThreadPool().shutdown();
        getXiaomingBot().getReceptionistManager().removeReceptionist(getUser().getQQ());
    }

    default ReceptionTask getGroupTask(long group) {
        return getGroupTasks().get(group);
    }

    default ReceptionTask getTempTask(long group)  {
        return getTempTasks().get(group);
    }

    /**
     * 获得或新建一个私聊接待任务
     * @return 私聊接待任务
     */
    Map<Long, ReceptionTask> getGroupTasks();

    Map<Long, ReceptionTask> getTempTasks();

    ReceptionTask getPrivateTask();

    void setPrivateTask(ReceptionTask task);

    void onGroupMessage(Member member, String message);

    void onTempMessage(Member member, String message);

    void onPrivateMessage(Friend friend, String message);

    Map<String, ReceptionTask> getReceptionTasks();
}