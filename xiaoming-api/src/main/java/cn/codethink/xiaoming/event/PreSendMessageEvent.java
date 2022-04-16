package cn.codethink.xiaoming.event;

/**
 * 即将发送消息事件
 *
 * @author Chuanwise
 */
public interface PreSendMessageEvent
    extends SendMessageEvent, CancellableEvent {
}
