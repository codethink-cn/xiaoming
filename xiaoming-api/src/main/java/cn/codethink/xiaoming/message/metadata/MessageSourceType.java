package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.IM;
import cn.codethink.xiaoming.annotation.ExpectantAPI;

/**
 * 消息源类型，只用于离线消息源
 *
 * @author Chuanwise
 */
public enum MessageSourceType {
    
    /**
     * 好友消息
     */
    FRIEND,
    
    /**
     * 群消息
     */
    @ExpectantAPI(IM.QQ)
    GROUP,
    
    /**
     * 频道消息
     */
    CHANNEL,
    
    /**
     * 群员消息
     */
    MEMBER,
    
    /**
     * 陌生人消息
     */
    STRANGER,
}
