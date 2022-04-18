package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.contact.Group;
import cn.codethink.xiaoming.contact.GroupMember;
import cn.codethink.xiaoming.contact.GroupSender;

/**
 * 来自群聊的消息源
 *
 * @author Chuanwise
 */
public interface FromGroupMessageSource
    extends FromMassMessageSource {
    
    /**
     * 获取集体
     *
     * @return 集体
     */
    @Override
    Group getTarget();
    
    /**
     * 获取消息发送方
     *
     * @return 消息发送方
     */
    @Override
    GroupSender getSender();
    
    /**
     * 获取集体
     *
     * @return 集体
     */
    @Override
    Group getMass();
}
