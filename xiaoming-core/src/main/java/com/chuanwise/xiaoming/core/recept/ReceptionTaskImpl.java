package com.chuanwise.xiaoming.core.recept;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.exception.InteractorTimeoutException;
import com.chuanwise.xiaoming.api.exception.ReceptCancelledException;
import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.account.AccountEventImpl;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Member;

import java.util.List;
import java.util.Objects;

/**
 * 交互器的响应任务
 * @author Chuanwise
 */
@Getter
public class ReceptionTaskImpl extends HostObjectImpl implements ReceptionTask {
    final Receptionist receptionist;
    String identify;
    final List<String> recentMessages;

    Thread thread;

    volatile boolean busy = false;
    volatile boolean running = false;

    Friend friend;
    Member member;
    volatile boolean temp;

    private ReceptionTaskImpl(Receptionist receptionist, List<String> recentMessages) {
        super(receptionist.getXiaomingBot());
        this.receptionist = receptionist;
        this.recentMessages = recentMessages;
    }

    /**
     * 新建群接待任务
     * @param receptionist 接待员
     * @param member 群成员
     * @return 群接待任务
     */
    public static ReceptionTask groupTask(Receptionist receptionist, Member member) {
        final ReceptionTaskImpl task = new ReceptionTaskImpl(receptionist, receptionist.getUser().getOrPutRecentGroupMessages(member.getGroup().getId()));
        task.member = member;
        task.temp = false;
        return task;
    }

    /**
     * 新建临时会话接待任务
     * @param receptionist 接待员
     * @param member 临时会话成员
     * @return 临时会话接待任务
     */
    public static ReceptionTask tempTask(Receptionist receptionist, Member member) {
        final ReceptionTaskImpl task = new ReceptionTaskImpl(receptionist, receptionist.getUser().getOrPutRecentTempMessages(member.getGroup().getId()));
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
        final ReceptionTaskImpl task = new ReceptionTaskImpl(receptionist, receptionist.getUser().getRecentPrivateMessage());
        task.friend = friend;
        return task;
    }

    @Override
    public XiaomingUser getUser() {
        return getReceptionist().getUser();
    }

    @Override
    public void recept() throws Exception {
        final XiaomingUser user = getReceptionist().getUser();

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
    }

    @Override
    public void onMessage(String message) {
        recentMessages.add(message.trim());
        synchronized (recentMessages) {
            recentMessages.notifyAll();
        }
    }

    @Override
    public void stop() {
        running = false;
        busy = false;

        thread.interrupt();
        if (inGroup()) {
            receptionist.getGroupTasks().remove(member.getGroup().getId());
        } else if (inTemp()) {
            receptionist.getTempTasks().remove(member.getGroup().getId());
        } else {
            receptionist.setPrivateTask(null);
        }
        unregister();
    }

    void register() {
        thread = Thread.currentThread();
        identify = "reception-task[" + (Objects.nonNull(member) ? (temp ? "temp" : "group") + ":" + member.getGroup().getId() : "private") + "]" +
                "(" + (Objects.nonNull(member) ? member.getId() : friend.getId()) + ")";

        // 设置线程身份
        thread.setName(identify);
        receptionist.getReceptionTasks().put(identify, this);

        if (inGroup()) {
            receptionist.getGroupTasks().put(member.getGroup().getId(), this);
        } else if (inTemp()) {
            receptionist.getTempTasks().put(member.getGroup().getId(), this);
        } else {
            receptionist.setPrivateTask(this);
        }
    }

    void unregister() {
        receptionist.getReceptionTasks().remove(identify);

        if (inGroup()) {
            receptionist.getGroupTasks().remove(member.getGroup().getId());
        } else if (inTemp()) {
            receptionist.getTempTasks().remove(member.getGroup().getId());
        } else {
            receptionist.setPrivateTask(null);
        }
    }

    @Override
    public void run() {
        running = true;
        final XiaomingUser user = getUser();
        register();

        try {
            busy = true;
            recept();
        } catch (ReceptCancelledException | InteractorTimeoutException exception) {
        } catch (Throwable throwable) {
            getLog().error("和用户" + user.getCompleteName() + "交互时出现异常", throwable);
            user.sendPrivateError("小明遇到了一个问题，这个问题已经上报了，期待更好的小明吧 {}", getXiaomingBot().getWordManager().get("happy"));
            getXiaomingBot().getReportMessageManager().addThrowableMessage(user, throwable);
        } finally {
            busy = false;
        }

        // 自动执行结束时，running 还是 true，所以手动执行 stop
        // 当前线程可能是在自己的 recentMessage 上等待，也可能是在别人的 recentMessage 上。咱们就粗暴打断
        // 如果是手动关闭，手动关闭
        if (running) {
            stop();
        } else {
            running = false;
        }
    }
}
