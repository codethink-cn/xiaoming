package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.util.MessageCodeTexts;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.basic.Text
 */
@Data
public class TextImpl
    extends AbstractBasicMessage
    implements Text {
    
    private final String text;
    
    private final String messageCode;
    
    public TextImpl(String text) {
        Preconditions.objectArgumentNonEmpty(text, "text");
        
        this.text = text;
        this.messageCode = MessageCodeTexts.toTextMessageCode(text);
    }
    
    @Override
    public String serializeToMessageCode() {
        return messageCode;
    }
    
    @Override
    public String serializeToMessageSummary() {
        return text;
    }
}
