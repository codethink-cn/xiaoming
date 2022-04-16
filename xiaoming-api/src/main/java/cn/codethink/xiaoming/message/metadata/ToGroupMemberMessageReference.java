package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.contact.GroupMember;

/**
 * 发送给群临时会话的消息引用
 *
 * @author Chuanwise
 */
public interface ToGroupMemberMessageReference
    extends ToMemberMessageReference {
    
    /**
     * 获取消息接收方
     *
     * @return 消息接收方
     */
    @Override
    GroupMember getTarget();
}
