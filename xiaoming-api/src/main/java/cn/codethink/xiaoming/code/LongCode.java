package cn.codethink.xiaoming.code;

import cn.codethink.common.collection.MapAdapter;
import cn.codethink.common.util.Maps;
import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * long 用户编码
 *
 * @author Chuanwise
 */
@Data
public class LongCode
    implements Code, Comparable<LongCode> {
    
    protected final long code;
    
    /**
     * LongCode 缓存池，应该是 SoftHashMap，但因为 JDK8，所以没有使用
     */
    private static final Map<Long, LongCode> INSTANCES = new HashMap<>();
    
    private LongCode(long code) {
        this.code = code;
    }
    
    public static LongCode valueOf(long code) {
        return Maps.getOrPutGet(INSTANCES, code, () -> new LongCode(code));
    }
    
    @Override
    public int compareTo(LongCode longCode) {
        return Long.compare(this.code, longCode.code);
    }
    
    @Override
    public String toMessageCode() {
        return "long:" + code;
    }
    
    @Override
    public String toContent() {
        return String.valueOf(code);
    }
}
