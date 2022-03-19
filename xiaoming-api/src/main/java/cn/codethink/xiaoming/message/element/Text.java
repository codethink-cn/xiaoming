package cn.codethink.xiaoming.message.element;

import cn.codethink.xiaoming.message.element.AbstractMessageElement;
import cn.codethink.common.util.Preconditions;
import lombok.Data;

/**
 * 普通字符串消息元素
 *
 * @author Chuanwise
 */
@Data
public class Text
        extends AbstractMessageElement {
    
    protected final String text;
    
    public Text(String text) {
        Preconditions.namedArgumentNonEmpty(text, "text");
        
        this.text = text;
    }
    
    @Override
    public String toMessageCode() {
        return text;
    }
    
    @Override
    public String toContent() {
        return text;
    }
}
