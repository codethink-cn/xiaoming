package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.code.Code;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see SingletonAt
 */
@Data
public class SingletonAtImpl
    extends AbstractBasicMessage
    implements SingletonAt {
    
    private final Code targetCode;
    
    public SingletonAtImpl(Code targetCode) {
        Preconditions.objectNonNull(targetCode, "target code");
        
        this.targetCode = targetCode;
    }
}
