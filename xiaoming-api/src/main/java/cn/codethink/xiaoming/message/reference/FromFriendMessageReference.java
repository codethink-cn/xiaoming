package cn.codethink.xiaoming.message.reference;

import cn.codethink.xiaoming.contact.Friend;

/**
 * 来自好友的消息源
 *
 * @author Chuanwise
 */
public interface FromFriendMessageReference
    extends IncomingOnlineMessageReference {
    
    /**
     * 获取消息发送方
     *
     * @return 消息发送方
     */
    @Override
    Friend getSender();
}
