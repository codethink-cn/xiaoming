package cn.codethink.xiaoming.message;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.annotation.InternalAPI;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.code.LongCode;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 资源池
 *
 * @author Chuanwise
 */
@InternalAPI
@SuppressWarnings("all")
public class ResourcePool
        extends AbstractBotObject {
    
    public ResourcePool(Bot bot) {
        super(bot);
    }
    
    /**
     * 消息池
     */
    protected final Map<Code, Message> messages = new ConcurrentHashMap<>();
    
    /**
     * 消息码
     */
    protected final AtomicLong messageCode = new AtomicLong(0);
    
    /**
     * 获取消息池
     *
     * @return 消息池
     */
    public Map<Code, Message> getMessages() {
        return Collections.unmodifiableMap(messages);
    }
    
    /**
     * 分配消息码
     *
     * @return 消息码
     */
    public Code allocateMessageCode() {
        return LongCode.valueOf(messageCode.incrementAndGet());
    }
    
    /**
     * 将消息加入缓存中
     *
     * @param message 消息
     * @return 缓存
     */
    public Message cacheMessage(Message message) {
        Preconditions.namedArgumentNonNull(message, "message");
    
        final Code code = message.getCode();
        messages.put(code, message);
        
        return message;
    }
}
