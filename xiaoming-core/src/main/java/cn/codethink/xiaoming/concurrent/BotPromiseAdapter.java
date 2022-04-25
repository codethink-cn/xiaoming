package cn.codethink.xiaoming.concurrent;

import cn.chuanwise.common.concurrent.Promise;
import cn.chuanwise.common.concurrent.PromiseAdapter;
import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import lombok.Getter;

/**
 * @author Chuanwise
 *
 * @param <T> 结果值类型
 *
 * @see cn.codethink.xiaoming.concurrent.BotPromise
 */
@Getter
public class BotPromiseAdapter<T>
    extends PromiseAdapter<T>
    implements BotPromise<T> {
    
    private final Bot bot;
    
    public BotPromiseAdapter(Bot bot, Promise<T> promise) {
        super(promise);
    
        Preconditions.objectNonNull(bot, "bot");
        
        this.bot = bot;
    }
}
