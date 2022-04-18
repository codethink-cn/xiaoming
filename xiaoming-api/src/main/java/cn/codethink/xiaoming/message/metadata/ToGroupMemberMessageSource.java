package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.contact.GroupMember;

/**
 * 发送给群临时会话的消息源
 *
 * @author Chuanwise
 */
public interface ToGroupMemberMessageSource
    extends ToMemberMessageSource {
    
    /**
     * 获取消息接收方
     *
     * @return 消息接收方
     */
    @Override
    GroupMember getTarget();
}
