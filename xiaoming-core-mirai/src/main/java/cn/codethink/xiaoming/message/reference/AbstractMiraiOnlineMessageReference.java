package cn.codethink.xiaoming.message.reference;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.message.Message;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.concurrent.TimeUnit;

/**
 * mirai 消息源
 *
 * @author Chuanwise
 */
@Getter
public abstract class AbstractMiraiOnlineMessageReference
    implements OnlineMessageReference {
    
    protected final OnlineMessageSource miraiMessageSource;
    
    protected final Sender sender;
    
    protected final ContactOrBot target;
    
    protected final MessageSourceType messageSourceType;
    
    protected final long timestamp;
    
    @Setter
    private Message message;
    
    public AbstractMiraiOnlineMessageReference(OnlineMessageSource miraiMessageSource, Sender sender, ContactOrBot target) {
        Preconditions.objectNonNull(miraiMessageSource, "mirai message source");
        Preconditions.objectNonNull(sender, "sender");
        Preconditions.objectNonNull(target, "target");
        
        this.miraiMessageSource = miraiMessageSource;
        this.sender = sender;
        this.target = target;
        this.messageSourceType = MiraiMessageSourceType.fromMirai(miraiMessageSource);
        this.timestamp = TimeUnit.SECONDS.toMillis(miraiMessageSource.getTime());
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
}
