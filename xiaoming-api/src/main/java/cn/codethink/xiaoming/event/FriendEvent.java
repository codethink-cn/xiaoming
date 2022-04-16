package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.Friend;

/**
 * 和好友相关的事件
 *
 * @author Chuanwise
 */
public interface FriendEvent
    extends Event {
    
    /**
     * 获取好友
     *
     * @return 好友
     */
    Friend getFriend();
}
