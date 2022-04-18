package cn.codethink.xiaoming.message.metadata;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.util.Mirais;
import lombok.Getter;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * mirai 离线消息源
 *
 * @author Chuanwise
 */
@Getter
public class MiraiOfflineMessageSource
    implements OfflineMessageSource, AutoSerializable {
    
    private final MessageChain originalMessage;
    
    private final net.mamoe.mirai.message.data.OfflineMessageSource miraiMessageSource;
    
    private final MessageSourceType messageSourceType;
    
    private final Code botCode;
    
    private final Code sourceCode;
    
    private final Code targetCode;
    
    private final long timestamp;
    
    private final Message message;
    
    public MiraiOfflineMessageSource(net.mamoe.mirai.message.data.OfflineMessageSource miraiMessageSource,
                                     Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(miraiMessageSource, "message source");
        
        this.originalMessage = miraiMessageSource.getOriginalMessage();
        this.miraiMessageSource = miraiMessageSource;
        this.messageSourceType = Mirais.toXiaoMing(miraiMessageSource.getKind());
        this.botCode = Code.ofLong(miraiMessageSource.getBotId());
        this.sourceCode = Code.ofLong(miraiMessageSource.getFromId());
        this.targetCode = Code.ofLong(miraiMessageSource.getTargetId());
        this.timestamp = TimeUnit.SECONDS.toMillis(miraiMessageSource.getTime());
        
        this.message = Mirais.toXiaoMing(miraiMessageSource.getOriginalMessage(), properties);
    }
    
    @Override
    public MessageSourceType getMessageSourceType() {
        return messageSourceType;
    }
    
    @Override
    public Message getMessage() {
        return message;
    }
}
