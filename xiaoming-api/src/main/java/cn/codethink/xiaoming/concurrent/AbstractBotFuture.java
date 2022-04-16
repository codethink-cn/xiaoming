package cn.codethink.xiaoming.concurrent;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Chuanwise
 * @see cn.codethink.xiaoming.concurrent.BotFuture
 * @param <T> 结果类型
 */
public abstract class AbstractBotFuture<T>
    extends AbstractBotTask
    implements BotFuture<T> {
    
    public AbstractBotFuture(Bot bot) {
        super(bot);
    }
    
    @Override
    public T get(long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        Preconditions.nonNull(timeUnit, "time unit");
        Preconditions.argument(timeout >= 0, "timeout must be bigger than or equals to 0!");
        
        return get(timeUnit.toMillis(timeout));
    }
}
