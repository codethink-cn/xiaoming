package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.compound.SingletonCompoundMessage;

import java.util.Objects;

/**
 * 抽象基础消息
 *
 * @author Chuanwise
 */
public abstract class AbstractBasicMessage
    implements BasicMessage {
    
    /**
     * 唯一的实例。因为 {@link CompoundMessage} 应该是不可变的，因此只需要产生一次，
     * 未来只需要不断获取这个实例即可。
     */
    private SingletonCompoundMessage compoundMessage;
    
    @Override
    public CompoundMessage asCompoundMessage() {
        if (Objects.isNull(compoundMessage)) {
            compoundMessage = new SingletonCompoundMessage(this);
        }
        
        return compoundMessage;
    }
}
