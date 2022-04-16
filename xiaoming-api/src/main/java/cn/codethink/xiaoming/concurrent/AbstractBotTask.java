package cn.codethink.xiaoming.concurrent;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @see BotTask
 * @author Chuanwise
 */
public abstract class AbstractBotTask
    extends AbstractBotObject
    implements BotTask {
    
    /**
     * 执行监听器
     */
    protected final List<BotTaskListener> listeners = new CopyOnWriteArrayList<>();
    
    /**
     * 错误异常信息
     */
    protected volatile Throwable cause;
    
    public AbstractBotTask(Bot bot) {
        super(bot);
    }
    
    @Override
    public Throwable getCause() {
        return cause;
    }
    
    @Override
    public void addListener(BotTaskListener botTaskListener) {
        Preconditions.nonNull(botTaskListener, "bot listener");
        
        listeners.add(botTaskListener);
        
        if (isDone()) {
            botTaskListener.listen(this);
        }
    }
    
    @Override
    public boolean isSucceed() {
        return isDone() && Objects.isNull(cause);
    }
    
    @Override
    public boolean isFailed() {
        return isDone() && Objects.nonNull(cause);
    }
    
    @Override
    public boolean await(long timeout, TimeUnit timeUnit) throws InterruptedException {
        Preconditions.nonNull(timeUnit, "time unit");
        Preconditions.argument(timeout >= 0, "duration must be bigger than or equals to 0!");
        
        return await(timeUnit.toMillis(timeout));
    }
    
    @Override
    public void awaitUninterruptibly(long timeout, TimeUnit timeUnit) {
        Preconditions.nonNull(timeUnit, "time unit");
        Preconditions.argument(timeout >= 0, "duration must be bigger than or equals to 0!");
        
        awaitUninterruptibly(timeUnit.toMillis(timeout));
    }
}
