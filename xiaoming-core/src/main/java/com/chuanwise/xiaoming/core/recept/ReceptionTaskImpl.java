package com.chuanwise.xiaoming.core.recept;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.account.AccountEvent;
import com.chuanwise.xiaoming.api.exception.InteractorTimeoutException;
import com.chuanwise.xiaoming.api.exception.ReceptCancelledException;
import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.limit.CallLimitConfig;
import com.chuanwise.xiaoming.api.limit.UserCallLimitManager;
import com.chuanwise.xiaoming.api.limit.UserCallLimiter;
import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.TimeUtil;
import com.chuanwise.xiaoming.core.account.AccountEventImpl;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Member;

import javax.security.auth.login.AccountException;
import java.util.List;
import java.util.Objects;

/**
 * 交互器的响应任务
 * @author Chuanwise
 */
@Getter
public class ReceptionTaskImpl extends HostObjectImpl implements ReceptionTask {
    final Receptionist receptionist;
    final List<String> recentMessage;
    final XiaomingUser user;

    Thread thread;

    int recentFreeTime = 0;
    volatile boolean busy = false;
    volatile boolean running = true;

    Friend friend;
    Member member;
    volatile boolean temp;

    private ReceptionTaskImpl(Receptionist receptionist, List<String> recentMessage) {
        super(receptionist.getXiaomingBot());
        this.receptionist = receptionist;
        this.recentMessage = recentMessage;
        this.user = receptionist.getUser();
    }

    /**
     * 新建群接待任务
     * @param receptionist 接待员
     * @param member 群成员
     * @return 群接待任务
     */
    public static ReceptionTask groupTask(Receptionist receptionist, Member member) {
        final XiaomingUser user = receptionist.getUser();
        final ReceptionTaskImpl task = new ReceptionTaskImpl(receptionist, user.getOrPutRecentGroupMessages(member.getGroup().getId()));
        task.member = member;
        task.temp = false;
        return task;
    }

    public static ReceptionTask externalTask(Receptionist receptionist, Member member) {
        return groupTask(receptionist, member);
    }

    /**
     * 新建临时会话接待任务
     * @param receptionist 接待员
     * @param member 临时会话成员
     * @return 临时会话接待任务
     */
    public static ReceptionTask tempTask(Receptionist receptionist, Member member) {
        final XiaomingUser user = receptionist.getUser();
        final ReceptionTaskImpl task = new ReceptionTaskImpl(receptionist, user.getOrPutRecentTempMessages(member.getGroup().getId()));
        task.member = member;
        task.temp = true;
        return task;
    }

    /**
     * 新建私聊接待任务
     * @param receptionist 接待员
     * @return 私聊接待任务
     */
    public static ReceptionTask privateTask(Receptionist receptionist, Friend friend) {
        final XiaomingUser user = receptionist.getUser();
        final ReceptionTaskImpl task = new ReceptionTaskImpl(receptionist, user.getRecentPrivateMessage());
        task.friend = friend;
        return task;
    }

    @Override
    public XiaomingUser getUser() {
        return getReceptionist().getUser();
    }

    @Override
    public void recept() {
        busy = true;

        try {
            if (getXiaomingBot().getInteractorManager().onInput(user)) {
                final AccountEventImpl event;
                if (Objects.nonNull(member)) {
                    event = new AccountEventImpl(member.getGroup().getId(), user.getMessage());
                } else {
                    event = new AccountEventImpl(user.getMessage());
                }
                if (inTemp()) {
                    event.setTemp(true);
                }
                final Account account = user.getOrPutAccount();
                account.addCommand(event);
                getXiaomingBot().getRegularPreserveManager().readySave(account);

                if (inGroup()) {
                    getXiaomingBot().getUserCallLimitManager().getGroupCallLimiter().addCallRecord(user.getQQ());
                } else {
                    getXiaomingBot().getUserCallLimitManager().getPrivateCallLimiter().addCallRecord(user.getQQ());
                }
            } else {
                user.clearBuffer();
            }
        } catch (ReceptCancelledException | InteractorTimeoutException exception) {
        } catch (Exception exception) {
            getLog().error("和用户" + user.getCompleteName() + "交互时出现异常", exception);
            user.sendPrivateError("小明遇到了一个问题，这个问题已经上报了，期待更好的小明吧 {}", getXiaomingBot().getWordManager().get("happy"));
            getXiaomingBot().getReportMessageManager().addThrowableMessage(user, exception);
            forceStop();
        } finally {
            busy = false;
        }
    }

    @Override
    public void stop() {
        if (busy) {
            throw new XiaomingRuntimeException("can not cancel a busy reception task.");
        }
        forceStop();
    }

    @Override
    public void forceStop() {
        running = false;
        synchronized (recentMessage) {
            recentMessage.notifyAll();
        }
        if (inGroup()) {
            final long group = getMember().getGroup().getId();
            getReceptionist().removeGroupTask(group);
        } else if (inPrivate()) {
            getReceptionist().removePrivateTask();
        } else if (inTemp()) {
            getReceptionist().removeTempTask(getMember().getId());
        } else {
            getReceptionist().removeExternalTask();
        }
    }

    @Override
    public void onMessage(String message) {
        recentMessage.add(message.trim());
        synchronized (recentMessage) {
            recentMessage.notifyAll();
        }
    }

    @Override
    public void run() {
        if (Objects.nonNull(thread)) {
            throw new XiaomingRuntimeException("multiple reception task thread");
        } else {
            thread = Thread.currentThread();
            final String threadName = "reception-task[" + (Objects.nonNull(member) ? (temp ? "temp" : "group") + ":" + member.getGroup().getId() : "private") + "]" +
                    "(" + (Objects.nonNull(member) ? member.getId() : friend.getId()) + ")";
            thread.setName(threadName);
            receptionist.getReceptionTasks().put(threadName, this);
        }

        while (!getXiaomingBot().isStop() && recentFreeTime < NO_RECEIPT_TIME && running) {
            if (Objects.nonNull(getMessage()) && !getXiaomingBot().isStop() && running) {
                recept();
                recentFreeTime = 0;
            }
            long lastestTime = System.currentTimeMillis() + RECEIPT_PERIOD;
            try {
                synchronized (recentMessage) {
                    recentMessage.wait(RECEIPT_PERIOD);
                }
            } catch (InterruptedException ignored) {
            }
            // 在最近输入身上等待，直到 ListenerManager 唤醒接待员或超时唤醒
            if (System.currentTimeMillis() > lastestTime) {
                // 正常退出的话肯定就是超时了
                recentFreeTime++;
            }
        }
        // 超时关闭的
        if (running) {
            stop();
        }
    }
}
