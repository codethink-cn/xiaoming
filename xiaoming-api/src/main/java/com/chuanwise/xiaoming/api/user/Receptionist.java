package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.object.HostObject;
import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Member;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * 小明接待员
 */
public interface Receptionist extends HostObject {
    ExecutorService getThreadPool();

    XiaomingUser getUser();

    default void optimize() {
        // 用上述方法删除那些空闲的线程
        getGroupTasks().values().forEach(task -> task.optimize());
        getTempTasks().values().forEach(task -> task.optimize());

        if (Objects.nonNull(getPrivateTask())) {
            getPrivateTask().optimize();
        }

        if (Objects.nonNull(getExternalTask())) {
            getExternalTask().optimize();
        }

        // 如果大家都是空，就销毁接待员
        if (getGroupTasks().isEmpty() && getTempTasks().isEmpty() && Objects.isNull(getPrivateTask())) {
            getXiaomingBot().getReceptionistManager().removeReceptionist(getUser().getQQ());
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
    }

    default void forceStop() {
        getGroupTasks().values().forEach(ReceptionTask::forceStop);
        getTempTasks().values().forEach(ReceptionTask::forceStop);
        if (Objects.nonNull(getPrivateTask())) {
            getPrivateTask().forceStop();
        }
        getThreadPool().shutdown();
    }

    default ReceptionTask getGroupTask(long group) {
        return getGroupTasks().get(group);
    }

    /**
     * 获得或新建一个群接待任务
     * @param group 群号
     * @return 群接待任务。如果这个群不是小明的响应群，返回 {@code null}
     */
    default ReceptionTask getOrPutGroupTask(long group, Member member) {
        final ResponseGroup responseGroup = getXiaomingBot().getResponseGroupManager().forCode(group);
        if (Objects.nonNull(responseGroup)) {
            return getOrPutGroupTask(responseGroup, member);
        } else {
            return null;
        }
    }

    ReceptionTask getOrPutGroupTask(ResponseGroup responseGroup, Member member);

    default ReceptionTask getTempTask(long group)  {
        return getTempTasks().get(group);
    }

    /**
     * 获得或新建一个临时会话接待任务
     * @param group 群号
     * @return 临时会话接待任务。如果这个群不是小明的响应群，返回 {@code null}
     */
    default ReceptionTask getOrPutTempTask(long group, Member member) {
        final ResponseGroup responseGroup = getXiaomingBot().getResponseGroupManager().forCode(group);
        if (Objects.nonNull(responseGroup)) {
            return getOrPutTempTask(responseGroup, member);
        } else {
            return null;
        }
    }

    ReceptionTask getOrPutTempTask(ResponseGroup responseGroup, Member member);

    /**
     * 获得或新建一个私聊接待任务
     * @return 私聊接待任务
     */
    ReceptionTask getOrPutPrivateTask(Friend friend);

    Map<Long, ReceptionTask> getGroupTasks();

    Map<Long, ReceptionTask> getTempTasks();

    ReceptionTask getPrivateTask();

    ReceptionTask getExternalTask();

    ReceptionTask getOrPutExternalTask(Member member);

    void setPrivateTask(ReceptionTask task);

    void setExternalTask(ReceptionTask task);

    default void removeGroupTask(long group) {
        getGroupTasks().remove(group);
    }

    default void removeTempTask(long group) {
        getTempTasks().remove(group);
    }

    void removePrivateTask();

    void removeExternalTask();
}
