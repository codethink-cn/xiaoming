package cn.codethink.xiaoming.message.element;

import cn.codethink.xiaoming.message.MessageCode;
import cn.codethink.xiaoming.message.parser.BasicMessageArgument;
import cn.codethink.xiaoming.message.parser.BasicMessageParser;
import cn.codethink.xiaoming.message.parser.MessageParsers;
import lombok.Data;
import net.mamoe.mirai.message.code.MiraiCode;
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
    
    static {
        // register parser
        MessageParsers.registerParsers(new Object() {
            
            @BasicMessageParser({"origin", "mirai", "?"})
            public MiraiOrigin parseMiraiOrigin(@BasicMessageArgument String miraiCode) {
                return new MiraiOrigin(MiraiCode.deserializeMiraiCode(miraiCode));
            }
        });
    }
    
    /**
     * 可以用 mirai 码表示的消息元素
     */
    private final MessageChain messageChain;
    
    @Override
    public String serializeToMessageCode() {
        return MessageCode.builder("origin")
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
