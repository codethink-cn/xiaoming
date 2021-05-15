package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.exception.InteractorTimeoutException;
import com.chuanwise.xiaoming.api.exception.ReceiptCancelledException;
import com.chuanwise.xiaoming.api.interactor.InteractorManager;
import com.chuanwise.xiaoming.api.user.Receiptionist;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.interactor.core.GlobalCommandInteractor;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import lombok.Getter;

import java.util.Objects;

/**
 * 小明接待员，实际上是主管和一个用户交互的线程
 * @author Chuanwise
 */
@Getter
public class ReceptionistImpl extends HostObjectImpl implements Receiptionist {
    final XiaomingUser user;

    int recentFreeTime = 0;

    volatile boolean receipting = false;

    volatile boolean running = true;

    public ReceptionistImpl(XiaomingUser user) {
        super(user.getXiaomingBot());
        this.user = user;
    }

    @Override
    public void receipt() {
        receipting = true;
        try {
            final boolean success;
            final InteractorManager interactorManager = getXiaomingBot().getInteractorManager();
            if (user.inGroup() && Objects.isNull(getXiaomingBot().getResponseGroupManager().fromCode(user.getGroup().getId()))) {
                // 用户在群聊招待范围外和其他范围内
                success = interactorManager.onInput(user, GlobalCommandInteractor.class);
            } else {
                // 用户在合理的招待范围内
                success = getXiaomingBot().getInteractorManager().onInput(user);
            }
            if (!success) {
                user.clearRecentInputs();
            }
        } catch (ReceiptCancelledException exception) {
        } catch (InteractorTimeoutException exception) {
        } catch (Exception exception) {
            getLog().error("和用户" + user.getCompleteName() + "时出现异常", exception);
            user.sendPrivateError("小明遇到了一个问题，这个问题已经上报了，期待更好的小明吧 {}", getXiaomingBot().getWordManager().get("happy"));
            getXiaomingBot().getErrorMessageManager().addThrowableMessage(user, exception);
        }
        user.setMessage(null);
        receipting = false;
    }

    @Override
    public void stop() {
        running = false;
        getXiaomingBot().getReceiptionistManager().removeReceiptionist(user.getQQ());
        synchronized (user) {
            user.notifyAll();
        }
    }

    @Override
    public void run() {
        while (!getXiaomingBot().isStop() && recentFreeTime < NO_RECEIPT_TIME && running) {
            // 如果携带新消息，则招待，否则不招待
            synchronized (user) {
                if (Objects.nonNull(user.getMessage())) {
                    receipt();
                    recentFreeTime = 0;
                } else {
                    recentFreeTime++;
                }
            }
            // 在用户身上等待，直到 ListenerManager 唤醒接待员或超时唤醒
            try {
                synchronized (user) {
                    user.wait(RECEIPT_PERIOD);
                }
            } catch (InterruptedException ignored) {
            }
        }
        stop();
    }
}
