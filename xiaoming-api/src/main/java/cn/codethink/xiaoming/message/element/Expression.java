package cn.codethink.xiaoming.message.element;

import cn.codethink.xiaoming.code.Code;
import cn.codethink.common.util.Preconditions;
import lombok.Data;

/**
 * 原生表情
 *
 * @author Chuanwise
 */
@Data
public class Expression
        extends AbstractMessageElement {
    
    protected final Code code;
    
    protected final String name;
    
    public Expression(Code code, String name) {
        Preconditions.namedArgumentNonNull(code, "code");
        Preconditions.namedArgumentNonEmpty(name, "name");
        
        this.code = code;
        this.name = name;
    }
    
    @Override
    public String toMessageCode() {
        return "[expression:code=" + code.toMessageCode() + "]";
    }
    
    @Override
    public String toContent() {
        return "[" + name + "]";
    }
}
