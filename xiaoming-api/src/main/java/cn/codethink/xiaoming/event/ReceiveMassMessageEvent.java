package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.Mass;
import cn.codethink.xiaoming.contact.MassSender;
import cn.codethink.xiaoming.contact.Member;

/**
 * 收到和集体相关消息的事件
 *
 * @author Chuanwise
 */
public interface ReceiveMassMessageEvent
    extends ReceiveMessageEvent {
    
    /**
     * 获取消息发送方
     *
     * @return 消息发送方
     */
    @Override
    MassSender getSender();
    
    /**
     * 获取消息发送方所在的集体
     *
     * @return 消息发送方所在的集体
     */
    default Mass getMass() {
        return getSender().getMass();
    }
}
