package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.message.MessageCodeBuilder;
import lombok.Data;
import net.mamoe.mirai.message.data.*;

/**
 * qq 实现层消息元素
 *
 * @author Chuanwise
 */
@Data
public class QqOrigin
    extends AbstractBasicMessage
    implements Origin, AutoSummarizable, AutoSerializable {
    
    /**
     * 可以用 qq 码表示的消息元素
     */
    private final MessageChain messageChain;
    
    @Override
    public String serializeToMessageCode() {
        return new MessageCodeBuilder("origin")
            .argument("qq")
            .argument(messageChain.serializeToMiraiCode())
            .build();
    }
    
    @Override
    public String getOriginalCode() {
        return messageChain.serializeToMiraiCode();
    }
}
