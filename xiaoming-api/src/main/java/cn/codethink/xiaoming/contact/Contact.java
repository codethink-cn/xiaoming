package cn.codethink.xiaoming.contact;

import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.MessagePushable;

/**
 * 可以发送消息的地方
 * 
 * @author Chuanwise
 */
public interface Contact
    extends BotObject, MessagePushable {
    
    /**
     * 获取编号
     *
     * @return 编号
     */
    Code getCode();
}
