package cn.codethink.xiaoming.message.element;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.SummarizableMessage;
import cn.codethink.xiaoming.util.Texts;
import lombok.Data;

/**
 * 普通文本消息元素
 *
 * @author Chuanwise
 */
@Data
public class Text
    extends AbstractBasicMessage
    implements BasicMessage, SummarizableMessage {
    
    /**
     * 原始文本
     */
    private final String text;
    
    /**
     * 序列化后的文本
     */
    private final String serializedText;
    
    /**
     * 构造一个文本基础消息
     *
     * @param text 原始文本
     * @throws NullPointerException text 为 null
     * @throws IllegalArgumentException text 为 ""
     */
    public Text(String text) {
        Preconditions.objectArgumentNonEmpty(text, "text");
        
        this.text = text;
        this.serializedText = Texts.serializeText(text);
    }
    
    @Override
    public String serializeToMessageCode() {
        return serializedText;
    }
    
    @Override
    public String serializeToSummary() {
        return text;
    }
}
