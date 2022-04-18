package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.compound.SingletonCompoundMessage;

import java.util.Objects;

/**
 * @author Chuanwise
 *
 * @see Mention
 * @see AllAccountMention
 */
public enum AllAccountMentionImpl
    implements AllAccountMention {
    
    /**
     * 全局唯一实例
     */
    INSTANCE;
    
    /**
     * 全局唯一实例
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
