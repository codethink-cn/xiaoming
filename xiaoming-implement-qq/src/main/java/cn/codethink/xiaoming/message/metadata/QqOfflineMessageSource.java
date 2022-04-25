package cn.codethink.xiaoming.message.metadata;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.util.Qqs;
import lombok.Getter;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * qq 离线消息源
 *
 * @author Chuanwise
 */
@Getter
public class QqOfflineMessageSource
    implements OfflineMessageSource, AutoSerializable {
    
    private final MessageChain originalMessage;
    
    private final net.mamoe.mirai.message.data.OfflineMessageSource qqMessageSource;
    
    private final MessageSourceType messageSourceType;
    
    private final Code botCode;
    
    private final Code sourceCode;
    
    private final Code targetCode;
    
    private final long timestamp;
    
    private final Message message;
    
    public QqOfflineMessageSource(net.mamoe.mirai.message.data.OfflineMessageSource qqMessageSource,
                                  Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(qqMessageSource, "message source");
        
        this.originalMessage = qqMessageSource.getOriginalMessage();
        this.qqMessageSource = qqMessageSource;
        this.messageSourceType = Qqs.toXiaoMing(qqMessageSource.getKind());
        this.botCode = Code.ofLong(qqMessageSource.getBotId());
        this.sourceCode = Code.ofLong(qqMessageSource.getFromId());
        this.targetCode = Code.ofLong(qqMessageSource.getTargetId());
        this.timestamp = TimeUnit.SECONDS.toMillis(qqMessageSource.getTime());
        
        this.message = Qqs.toXiaoMing(qqMessageSource.getOriginalMessage(), properties);
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
