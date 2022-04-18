package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.contact.Group;

/**
 * 发送到群的消息源
 *
 * @author Chuanwise
 */
public interface ToGroupMessageSource
    extends ToMassMessageSource {
    
    /**
     * 获取消息接收方
     *
     * @return 消息接收方
     */
    @Override
    Group getTarget();
    
    /**
     * 获取消息接收方
     *
     * @return 消息接收方
     */
    @Override
    Group getMass();
}
