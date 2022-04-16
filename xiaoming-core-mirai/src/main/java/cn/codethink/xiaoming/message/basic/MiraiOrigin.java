package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.MessageCodeBuilder;
import lombok.Data;
import net.mamoe.mirai.message.data.*;

/**
 * mirai 实现层消息元素
 *
 * @author Chuanwise
 */
@Data
public class MiraiOrigin
    extends AbstractBasicMessage
    implements Origin {
    
    /**
     * 可以用 mirai 码表示的消息元素
     */
    private final MessageChain messageChain;
    
    @Override
    public String serializeToMessageCode() {
        return new MessageCodeBuilder("origin")
            .argument("mirai")
            .argument(messageChain.serializeToMiraiCode())
            .build();
    }
    
    @Override
    public String getOriginalCode() {
        return messageChain.serializeToMiraiCode();
    }
    
    @Override
    public String serializeToSummary() {
        return messageChain.contentToString();
    }
}
