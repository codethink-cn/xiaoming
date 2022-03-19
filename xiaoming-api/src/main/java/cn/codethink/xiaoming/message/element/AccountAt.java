package cn.codethink.xiaoming.message.element;

import cn.codethink.xiaoming.code.Code;
import cn.codethink.common.util.Preconditions;
import lombok.Data;

/**
 * 用于 @ 人的 At
 *
 * @author Chuanwise
 */
@Data
public class AccountAt
        extends AbstractMessageElement
        implements At {
    
    protected final Code code;
    
    public AccountAt(Code code) {
        Preconditions.namedArgumentNonNull(code, "account code");
        
        this.code = code;
    }
    
    @Override
    public String toMessageCode() {
        return "[at:account=" + code.toMessageCode() + "]";
    }
    
    @Override
    public String toContent() {
        return '@' + code.toContent();
    }
}
