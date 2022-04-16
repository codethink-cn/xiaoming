package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.contact.Friend;

/**
 * 发送到好友的消息引用
 *
 * @author Chuanwise
 */
public interface ToFriendMessageReference
    extends OutgoingOnlineMessageReference {
    
    /**
     * 获取消息接收方
     *
     * @return 消息接收方
     */
    @Override
    Friend getTarget();
}
