package cn.codethink.xiaoming.message.metadata;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.util.Mirais;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.contact.PermissionDeniedException;
import net.mamoe.mirai.message.data.MessageSource;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * mirai 消息源
 *
 * @author Chuanwise
 */
@Getter
public abstract class AbstractMiraiOnlineMessageSource
    implements OnlineMessageSource, AutoSerializable {
    
    protected final net.mamoe.mirai.message.data.OnlineMessageSource miraiMessageSource;
    
    protected final Sender sender;
    
    protected final ContactOrBot target;
    
    protected final MessageSourceType messageSourceType;
    
    protected final long timestamp;
    
    protected volatile boolean recalled;
    
    @Setter
    private Message message;
    
    public AbstractMiraiOnlineMessageSource(net.mamoe.mirai.message.data.OnlineMessageSource miraiMessageSource,
                                            Sender sender,
                                            ContactOrBot target,
                                            Map<Property<?>, Object> properties) {
        
        Preconditions.objectNonNull(miraiMessageSource, "mirai message source");
        Preconditions.objectNonNull(sender, "sender");
        Preconditions.objectNonNull(target, "target");
        Preconditions.objectNonNull(properties, "properties");
        
        this.miraiMessageSource = miraiMessageSource;
        this.sender = sender;
        this.target = target;
        this.messageSourceType = Mirais.toXiaoMing(miraiMessageSource);
        this.timestamp = TimeUnit.SECONDS.toMillis(miraiMessageSource.getTime());
        
        this.message = Mirais.toXiaoMing(miraiMessageSource.getOriginalMessage(), properties);
    }
    
    @Override
    public Bot getBot() {
        return sender.getBot();
    }
    
    @Override
    public Code getBotCode() {
        return getBot().getCode();
    }
    
    @Override
    public Code getSourceCode() {
        if (sender instanceof UserOrBot) {
            return ((UserOrBot) sender).getCode();
        } else {
            return Code.ofLong(0);
        }
    }
    
    @Override
    public Code getTargetCode() {
        return target.getCode();
    }
    
    @Override
    public boolean recall() {
        // if message already be recalled,
        // return false directly
        if (recalled) {
            return false;
        }
        
        try {
            MessageSource.recall(miraiMessageSource);
            recalled = true;
            return true;
        } catch (PermissionDeniedException e) {
            throw new cn.codethink.xiaoming.exception.PermissionDeniedException(getBot());
        } catch (IllegalStateException e) {
            recalled = true;
            return false;
        }
    }
}
