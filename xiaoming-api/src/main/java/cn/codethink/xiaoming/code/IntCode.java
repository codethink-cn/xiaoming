package cn.codethink.xiaoming.code;

import cn.codethink.util.Maps;
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
    
    protected final int code;
    
    private static final Map<Integer, IntCode> INSTANCES = new HashMap<>();
    
    private IntCode(int code) {
        this.code = code;
    }
    
    public static IntCode valueOf(int code) {
        return Maps.getOrPutGet(INSTANCES, code, () -> new IntCode(code));
    }
    
    @Override
    public int compareTo(IntCode longCode) {
        return Integer.compare(this.code, longCode.code);
    }
    
    @Override
    public String toMessageCode() {
        return "int:" + code;
    }
    
    @Override
    public String toContent() {
        return String.valueOf(code);
    }
}
