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
public class MarketExpression
        extends AbstractMessageElement {
    
    protected final Code code;
    
    protected final String name;
    
    public MarketExpression(Code code, String name) {
        Preconditions.namedArgumentNonNull(code, "code");
        Preconditions.namedArgumentNonEmpty(name, "name");
        
        this.code = code;
        this.name = name;
    }
    
    @Override
    public String toMessageCode() {
        return "[market-expression:code=" + code.toMessageCode() + "]";
    }
    
    @Override
    public String toContent() {
        return "[" + name + "]";
    }
}
