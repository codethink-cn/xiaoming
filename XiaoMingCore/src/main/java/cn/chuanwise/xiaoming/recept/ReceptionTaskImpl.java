package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.exception.InteractInterrtuptedException;
import cn.chuanwise.xiaoming.exception.InteractExitedException;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import kotlinx.coroutines.TimeoutCancellationException;
import lombok.Getter;

import java.util.Objects;

/**
 * 交互器的响应任务
 * @author Chuanwise
 */
@Getter
public class ReceptionTaskImpl<U extends XiaomingUser<?>> extends ModuleObjectImpl implements ReceptionTask<U> {
    final U user;

    protected Message message;

    protected Thread thread;

    volatile boolean busy = false;
    volatile boolean running = false;

    protected ReceptionTaskImpl(U user, Message message) {
        super(user.getXiaomingBot());
        this.message = message;
        this.user = user;
    }

    public void stop() {
        if (busy && thread.isAlive()) {
            thread.interrupt();
        }

        busy = false;
        running = false;
    }

    @Override
    public Boolean call() throws Exception {
        final XiaomingUser user = getUser();
        if (Objects.nonNull(thread)) {
            getLogger().error("重新启动已经停止的接待任务：" + message);
        }

        thread = Thread.currentThread();
        running = true;

        try {
            busy = true;
            return xiaomingBot.getInteractorManager().interact(getUser(), message);
        } catch (InteractExitedException | InteractInterrtuptedException | TimeoutCancellationException exception) {
            return false;
        } finally {
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
}
