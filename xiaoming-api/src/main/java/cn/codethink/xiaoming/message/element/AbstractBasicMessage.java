package cn.codethink.xiaoming.message.element;

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
    
    private SingletonCompoundMessage compoundMessage;
    
    @Override
    public CompoundMessage asCompoundMessage() {
        if (Objects.isNull(compoundMessage)) {
            compoundMessage = new SingletonCompoundMessage(this);
        }
        
        return compoundMessage;
    }
}
