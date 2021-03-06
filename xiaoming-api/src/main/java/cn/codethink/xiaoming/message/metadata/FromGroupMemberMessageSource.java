package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.contact.Group;
import cn.codethink.xiaoming.contact.GroupMember;

/**
 * 来自群临时会话的消息源
 *
 * @author Chuanwise
 */
public interface FromGroupMemberMessageSource
    extends FromMemberMessageSource {
    
    /**
     * 获取消息发送方
     *
     * @return 消息发送方
     */
    @Override
    GroupMember getSender();
    
    /**
     * 获取集体
     *
     * @return 集体
     */
    @Override
    Group getMass();
}
