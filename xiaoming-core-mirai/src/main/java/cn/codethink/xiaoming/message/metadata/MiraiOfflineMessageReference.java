package cn.codethink.xiaoming.message.metadata;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.MiraiMessageSourceType;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.MessageCode;
import cn.codethink.xiaoming.message.MessageCodeBuilder;
import cn.codethink.xiaoming.message.MiraiMessageChain;
import lombok.Getter;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.OfflineMessageSource;

import java.util.concurrent.TimeUnit;

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
    
    private final MessageSourceType messageSourceType;
    
    private final Code botCode;
    
    private final Code sourceCode;
    
    private final Code targetCode;
    
    private final long timestamp;
    
    public MiraiOfflineMessageReference(OfflineMessageSource miraiMessageSource) {
        Preconditions.objectNonNull(miraiMessageSource, "message source");
        
        this.originalMessage = miraiMessageSource.getOriginalMessage();
        this.miraiMessageSource = miraiMessageSource;
        this.messageSourceType = MiraiMessageSourceType.fromMirai(miraiMessageSource.getKind());
        this.botCode = Code.ofLong(miraiMessageSource.getBotId());
        this.sourceCode = Code.ofLong(miraiMessageSource.getFromId());
        this.targetCode = Code.ofLong(miraiMessageSource.getTargetId());
        this.timestamp = TimeUnit.SECONDS.toMillis(miraiMessageSource.getTime());
    
        // TODO: 2022/4/16 set message
//        MiraiMessageChain.toCompoundMessage()
    }
    
    @Override
    public String serializeToMessageCode() {
        return new MessageCodeBuilder("source")
            .argument("mirai")
            .argument("offline")
            .argument("friend")
            .argument("")
            .build();
        // TODO: 2022/4/16 finish
    }
    
    @Override
    public MessageSourceType getMessageSourceType() {
        return messageSourceType;
    }
    
    @Override
    public Message getMessage() {
        return null;
    }
}
