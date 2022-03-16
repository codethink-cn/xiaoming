package cn.codethink.xiaoming.message;

import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.content.MessageContent;

/**
 * 在线消息
 *
 * @author Chuanwise
 */
public interface Message {
    
    /**
     * 获取消息内容
     *
     * @return 消息内容
     */
    MessageContent getMessageContent();
    
    /**
     * 获取时间戳
     *
     * @return 时间戳
     */
    long getTimeMillis();
    
    /**
     * 获取编号
     *
     * @return 编号
     */
    Code getCode();
}
