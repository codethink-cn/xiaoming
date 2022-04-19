package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.code.Code;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see SingletonAccountAt
 */
@Data
public class SingletonAccountAtImpl
    extends AbstractBasicMessage
    implements SingletonAccountAt {
    
    private final Code targetCode;
    
    public SingletonAccountAtImpl(Code targetCode) {
        Preconditions.objectNonNull(targetCode, "target code");
        
        this.targetCode = targetCode;
    }
}
