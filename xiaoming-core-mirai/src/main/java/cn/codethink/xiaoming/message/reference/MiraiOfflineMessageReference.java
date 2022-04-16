package cn.codethink.xiaoming.message.reference;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.MessageCode;
import lombok.Getter;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.OfflineMessageSource;

/**
 * mirai 离线消息源
 *
 * @author Chuanwise
 */
@Getter
public class MiraiOfflineMessageReference
    implements OfflineMessageReference {
    
    private final MessageChain originalMessage;
    
    private final OfflineMessageSource miraiMessageSource;
    
    public MiraiOfflineMessageReference(OfflineMessageSource miraiMessageSource) {
        Preconditions.objectNonNull(miraiMessageSource, "message source");
        
        this.originalMessage = miraiMessageSource.getOriginalMessage();
        this.miraiMessageSource = miraiMessageSource;
    }
    
    @Override
    public String serializeToMessageCode() {
        return MessageCode.builder("source")
            .argument("mirai")
            .argument("offline")
            .argument("friend")
            .argument("")
            .build();
    }
    
    @Override
    public MessageSourceType getMessageSourceType() {
        return null;
    }
    
    @Override
    public Code getBotCode() {
        return null;
    }
    
    @Override
    public Code getSourceCode() {
        return null;
    }
    
    @Override
    public Code getTargetCode() {
        return null;
    }
    
    @Override
    public long getTimestamp() {
        return 0;
    }
    
    @Override
    public Message getMessage() {
        return null;
    }
}
