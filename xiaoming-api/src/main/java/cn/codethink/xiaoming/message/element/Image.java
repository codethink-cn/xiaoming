package cn.codethink.xiaoming.message.element;

import cn.codethink.xiaoming.code.Code;
import cn.codethink.common.util.Preconditions;
import lombok.Data;

/**
 * 图片消息
 *
 * @author Chuanwise
 */
@Data
public class Image
        extends AbstractMessageElement {
    
    protected final Code code;
    
    public Image(Code code) {
        Preconditions.namedArgumentNonNull(code, "code");
        
        this.code = code;
    }
    
    @Override
    public String toMessageCode() {
        return "[image:code=" + code.toMessageCode() + "]";
    }
    
    @Override
    public String toContent() {
        return "[图片]";
    }
}

