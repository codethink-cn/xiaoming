package com.chuanwise.xiaoming.api.recept;

import com.chuanwise.xiaoming.api.exception.InteractorTimeoutException;
import com.chuanwise.xiaoming.api.exception.ReceptCancelledException;
import com.chuanwise.xiaoming.api.object.HostObject;
import com.chuanwise.xiaoming.api.object.XiaomingThread;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.TimeUtil;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Member;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public interface ReceptionTask extends HostObject, XiaomingThread {
    int NO_RECEIPT_TIME = 3;
    long RECEIPT_PERIOD = TimeUnit.MINUTES.toMillis(5);
    long NEXT_INPUT_MAX_WAIT_TIME = TimeUnit.MINUTES.toMillis(5);

    /**
     * 获得接待的用户
     * @return
     */
    XiaomingUser getUser();

    void recept();

    default void optimize() {
        if (!isBusy()) {
            stop();
        }
    }

    Receptionist getReceptionist();

    List<String> getRecentMessage();

    int getRecentFreeTime();

    boolean isBusy();

    boolean isRunning();

    Friend getFriend();

    Member getMember();

    boolean isTemp();

    Thread getThread();

    void onMessage(String message);

    default boolean isStop() {
        return !isRunning();
    }

    default boolean inGroup() {
        return Objects.nonNull(getMember()) && !isTemp();
    }

    default boolean inTemp() {
        return isTemp();
    }

    default boolean inPrivate() {
        return Objects.nonNull(getFriend());
    }

    default String getMessage() {
        final List<String> list = getRecentMessage();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(list.size() - 1 );
        }
    }

    default String nextInput(long timeout, Function<Void, Void> onTimeout) {
        final List<String> list = getRecentMessage();
        final int sizeBeforeWait = list.size();
        final long latestTime = System.currentTimeMillis() + timeout;

        try {
            synchronized (list) {
                list.wait(timeout);
            }
        } catch (InterruptedException ignored) {
        }
        if (System.currentTimeMillis() < latestTime) {
            // 增加了一条消息
            if (sizeBeforeWait + 1 == list.size()) {
                return list.get(sizeBeforeWait);
            } else {
                // 是被人打断的
                throw new ReceptCancelledException();
            }
        } else {
            onTimeout.apply(null);
            return null;
        }
    }

    default String nextInput(Function<Void, Void> onTimeout) {
        return nextInput(NEXT_INPUT_MAX_WAIT_TIME, onTimeout);
    }

    default String nextInput(long timeout) {
        return nextInput(timeout, para -> {
            getUser().sendMessage("你已经{}没有理小明啦，小明就不等待你的下一条消息啦", TimeUtil.toTimeString(NEXT_INPUT_MAX_WAIT_TIME));
            throw new InteractorTimeoutException();
        });
    }

    default String nextInput() {
        return nextInput(NEXT_INPUT_MAX_WAIT_TIME);
    }
}
