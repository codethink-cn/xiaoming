package cn.codethink.xiaoming.event;

/**
 * 即将发送好友消息事件
 *
 * @author Chuanwise
 */
public interface PreSendFriendMessageEvent
    extends PreSendMessageEvent, FriendEvent {
}
