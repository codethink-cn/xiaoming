package cn.codethink.xiaoming.code;

import cn.codethink.common.util.Numbers;
import cn.codethink.common.util.Preconditions;
import lombok.Data;

/**
 * long 码
 *
 * @author Chuanwise
 */
@Data
public class StringCode
    implements Code, Comparable<StringCode> {
    
    protected final String value;
    
    public StringCode(String value) {
        Preconditions.nonNull(value, "value");
        
        this.value = value;
    }
    
    @Override
    public int compareTo(StringCode stringCode) {
        return this.value.compareTo(stringCode.value);
    }
    
    @Override
    public long asLong() {
        final Long value = Numbers.parseLong(this.value);
        Preconditions.operationNonNull(value);
        return value;
    }
    
    @Override
    public int asInt() {
        final Integer value = Numbers.parseInt(this.value);
        Preconditions.operationNonNull(value);
        return value;
    }
    
    @Override
    public String asString() {
        return value;
    }
    
    @Override
    public String toString() {
        return "s," + value;
    }
}
