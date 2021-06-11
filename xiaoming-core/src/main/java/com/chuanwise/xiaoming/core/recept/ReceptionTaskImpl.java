package com.chuanwise.xiaoming.core.recept;

import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.exception.InteractorTimeoutException;
import com.chuanwise.xiaoming.api.exception.ReceptCancelledException;
import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.object.ModuleObjectImpl;
import lombok.Getter;

import java.util.List;

/**
 * 交互器的响应任务
 * @author Chuanwise
 */
@Getter
public abstract class ReceptionTaskImpl extends ModuleObjectImpl implements ReceptionTask {
    final Receptionist receptionist;
    final String identify;

    Thread thread;

    volatile boolean busy = false;
    volatile boolean running = false;

    protected ReceptionTaskImpl(Receptionist receptionist, String identify) {
        super(receptionist.getXiaomingBot());
        this.receptionist = receptionist;
        this.identify = identify;
    }

    @Override
    public abstract XiaomingUser getUser();

    public abstract void recept(Message message) throws Exception;

    public abstract void stop();

    protected abstract void register();

    protected abstract void unregister();

    @Override
    public final void run() {
        final XiaomingUser user = getUser();
        register();
        running = true;

        try {
            busy = true;
            final List<? extends Message> list = getRecentMessages();
            recept(list.get(list.size() - 1));
        } catch (ReceptCancelledException | InteractorTimeoutException exception) {
        } catch (Throwable throwable) {
            getLog().error("和用户" + user.getCompleteName() + "交互时出现异常", throwable);
            user.sendError("{internalError}");
            getXiaomingBot().getReportMessageManager().addThrowableMessage(user, throwable);
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
