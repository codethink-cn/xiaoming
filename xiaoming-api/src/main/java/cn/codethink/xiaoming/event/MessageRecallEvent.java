package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.message.compound.CompoundMessage;

/**
 * 消息撤回事件
 *
 * @author Chuanwise
 */
public interface MessageRecallEvent
    extends OnlineMessageEvent {
    
    /**
     * 获取被撤回的消息。
     *
     * <b>被撤回的消息有可能是 null！</b>
     * 消息可能发送于 Bot 启动之前，或缓存已被清除。在这些情况下该方法返回 null。
     *
     * @return 被撤回的消息或 null
     */
    @Override
    CompoundMessage getMessage();
}
