package cn.codethink.xiaoming.code;

import cn.codethink.common.util.Preconditions;
import lombok.Data;

/**
 * long 用户编码
 *
 * @author Chuanwise
 */
@Data
public class StringCode
    implements Code, Comparable<StringCode> {
    
    protected final String code;
    
    public StringCode(String code) {
        Preconditions.namedArgumentNonNull(code, "code");
        
        this.code = code;
    }
    
    @Override
    public int compareTo(StringCode stringCode) {
        return this.code.compareTo(stringCode.code);
    }
    
    @Override
    public String toMessageCode() {
        return "string:" + code;
    }
    
    @Override
    public String toContent() {
        return code;
    }
}
