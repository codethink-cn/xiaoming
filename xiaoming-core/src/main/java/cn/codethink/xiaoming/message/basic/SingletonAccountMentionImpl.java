package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.code.Code;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see SingletonAccountMention
 */
@Data
public class SingletonAccountMentionImpl
    extends AbstractBasicMessage
    implements SingletonAccountMention {
    
    private final Code targetCode;
    
    public SingletonAccountMentionImpl(Code targetCode) {
        Preconditions.objectNonNull(targetCode, "target code");
        
        this.targetCode = targetCode;
    }
}
