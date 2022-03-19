package cn.codethink.xiaoming.message.element;

import cn.codethink.xiaoming.code.Code;
import cn.codethink.common.util.Preconditions;
import lombok.Data;

/**
 * 闪照消息
 *
 * @author Chuanwise
 */
@Data
public class FlashImage
        extends AbstractMessageElement {
    
    protected final Code code;
    
    public FlashImage(Code code) {
        Preconditions.namedArgumentNonNull(code, "code");
        
        this.code = code;
    }
    
    @Override
    public String toMessageCode() {
        return "[flash:code=" + code.toMessageCode() + "]";
    }
    
    @Override
    public String toContent() {
        return "[闪照]";
    }
}
