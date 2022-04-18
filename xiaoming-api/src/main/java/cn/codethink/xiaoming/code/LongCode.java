package cn.codethink.xiaoming.code;

import cn.codethink.common.util.Maps;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * long 码
 *
 * @author Chuanwise
 */
@Data
public class LongCode
    implements Code, Comparable<LongCode> {
    
    protected final long value;
    
    /**
     * LongCode 缓存池，应该是 SoftHashMap，但因为 JDK8，所以没有使用
     */
    private static final Map<Long, LongCode> INSTANCES = new WeakHashMap<>();
    
    private LongCode(long value) {
        this.value = value;
    }
    
    public static LongCode valueOf(long code) {
        return Maps.getOrPutGet(INSTANCES, code, () -> new LongCode(code));
    }
    
    @Override
    public int compareTo(LongCode longCode) {
        return Long.compare(this.value, longCode.value);
    }
    
    @Override
    public long asLong() {
        return value;
    }
    
    @Override
    public int asInt() {
        return (int) value;
    }
    
    @Override
    public String asString() {
        return String.valueOf(value);
    }
    
    @Override
    public String toString() {
        return "l," + value;
    }
    
    @Override
    public Long getValue() {
        return value;
    }
}
