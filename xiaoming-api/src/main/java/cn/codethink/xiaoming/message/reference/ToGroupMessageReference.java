package cn.codethink.xiaoming.message.reference;

import cn.codethink.xiaoming.contact.Group;

/**
 * 发送到群的消息引用
 *
 * @author Chuanwise
 */
public interface ToGroupMessageReference
    extends ToMassMessageReference {
    
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
