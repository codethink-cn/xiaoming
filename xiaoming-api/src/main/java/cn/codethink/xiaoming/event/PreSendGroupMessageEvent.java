package cn.codethink.xiaoming.event;

/**
 * 即将发送群消息事件
 *
 * @author Chuanwise
 */
public interface PreSendGroupMessageEvent
    extends PreSendMassMessageEvent, GroupEvent {
}
