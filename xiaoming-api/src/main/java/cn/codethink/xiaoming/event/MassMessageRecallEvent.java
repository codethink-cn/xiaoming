package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.GroupSender;
import cn.codethink.xiaoming.contact.MassSender;
import cn.codethink.xiaoming.contact.Sender;
import cn.codethink.xiaoming.contact.UserOrBot;

/**
 * 集体中的消息撤回事件
 *
 * @author Chuanwise
 */
public interface MassMessageRecallEvent
    extends MessageRecallEvent, MassEvent {
    
    /**
     * 获取撤回操作人
     *
     * @return 撤回操作人
     */
    UserOrBot getOperator();
    
    /**
     * 获取消息发送方
     *
     * @return 消息发送方
     */
    @Override
    MassSender getSender();
}
