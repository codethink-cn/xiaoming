package cn.codethink.xiaoming.util;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.annotation.InternalAPI;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.Cached;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 会话缓存表
 *
 * @author Chuanwise
 */
@InternalAPI
@SuppressWarnings("all")
public class CachedContactMap<T, U extends Cached> {
    
    /**
     * 由原始类型构造缓存类型的方法
     */
    protected final Function<T, U> translator;
    
    /**
     * 启用缓存的方法
     */
    protected final Consumer<U> availableModifier;
    
    /**
     * 作废缓存的方法
     */
    protected final Consumer<U> unavailableModifier;
    
    /**
     * 获取缓存 ID 的方法
     */
    protected final Function<T, Code> codeGetter;
    
    /**
     * 有效缓存表
     */
    protected final Map<Code, U> available = new ConcurrentHashMap<>();
    
    /**
     * 无效缓存表
     */
    protected final Map<Code, U> unavailable = new ConcurrentHashMap<>();
    
    public CachedContactMap(Function<T, U> translator,
                            Consumer<U> availableModifier,
                            Consumer<U> unavailableModifier,
                            Function<T, Code> codeGetter) {
    
        Preconditions.objectNonNull(translator, "translator");
        Preconditions.objectNonNull(availableModifier, "available modifier");
        Preconditions.objectNonNull(unavailableModifier, "unavailable modifier");
        Preconditions.objectNonNull(codeGetter, "code getter");
        
        this.translator = translator;
        this.availableModifier = availableModifier;
        this.unavailableModifier = unavailableModifier;
        this.codeGetter = codeGetter;
    }
    
    public U getAvailable(T origin) {
        Preconditions.objectNonNull(origin, "origin");
        
        // check in available
        final Code code = codeGetter.apply(origin);
        final U availableInstance = available.get(code);
        if (Objects.nonNull(availableInstance)) {
            return availableInstance;
        }
    
        final U unavailableInstance = unavailable.remove(code);
        if (Objects.nonNull(unavailableInstance)) {
            availableModifier.accept(unavailableInstance);
            available.put(code, unavailableInstance);
            return unavailableInstance;
        }
    
        final U generated = translator.apply(origin);
        availableModifier.accept(generated);
        available.put(code, generated);
        return generated;
    }
    
    public U getUnavailable(T origin) {
        Preconditions.objectNonNull(origin, "origin");
        
        // check in available
        final Code code = codeGetter.apply(origin);
        final U unavailableInstance = unavailable.get(code);
        if (Objects.nonNull(unavailableInstance)) {
            return unavailableInstance;
        }
    
        final U availableInstance = available.remove(code);
        if (Objects.nonNull(availableInstance)) {
            unavailableModifier.accept(availableInstance);
            unavailable.put(code, availableInstance);
            return availableInstance;
        }
    
        final U generated = translator.apply(origin);
        unavailableModifier.accept(generated);
        unavailable.put(code, generated);
        return generated;
    }
    
    public U getAvailable(Code code) {
        return available.get(code);
    }
    
    public U getUnavailable(Code code) {
        return unavailable.get(code);
    }
    
    public Map<Code, U> getAvailable() {
        return available;
    }
    
    public Map<Code, U> getUnavailable() {
        return unavailable;
    }
}
