package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.spi.XiaoMing;

/**
 * 引用某条消息
 *
 * @author Chuanwise
 */
public interface Quote
    extends MessageMetadata, AutoSerializable {
    
    /**
     * 构造指向消息源的回复信息
     * 
     * @param source 消息源
     * @return 引用回复
     * @throws NullPointerException source 为 null
     */
    static Quote of(MessageSource source) {
        return XiaoMing.get().newQuote(source);
    }
    
    /**
     * 获取引用的目标
     *
     * @return 引用的目标
     */
    MessageSource getMessageSource();
}
