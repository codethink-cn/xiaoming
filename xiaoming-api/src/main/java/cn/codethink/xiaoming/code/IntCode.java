package cn.codethink.xiaoming.code;

import cn.codethink.common.util.Maps;
import cn.chuanwise.common.util.SoftMap;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * long 用户编码
 *
 * @author Chuanwise
 */
@Data
public class IntCode
    implements Code, Comparable<IntCode> {
    
    protected final int value;
    
    private static final Map<Integer, IntCode> INSTANCES = new SoftMap<>(new HashMap<>());
    
    private IntCode(int value) {
        this.value = value;
    }
    
    public static IntCode valueOf(int code) {
        return Maps.getOrPutGet(INSTANCES, code, () -> new IntCode(code));
    }
    
    @Override
    public int compareTo(IntCode longCode) {
        return Integer.compare(this.value, longCode.value);
    }
    
    @Override
    public long asLong() {
        return value;
    }
    
    @Override
    public int asInt() {
        return value;
    }
    
    @Override
    public String asString() {
        return String.valueOf(value);
    }
    
    @Override
    public String toString() {
        return "i:" + value;
    }
    
    @Override
    public Integer getValue() {
        return value;
    }
}
