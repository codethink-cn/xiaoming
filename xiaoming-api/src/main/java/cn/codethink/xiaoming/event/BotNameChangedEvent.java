package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.BotObject;

/**
 * Bot 修改昵称
 *
 * @author Chuanwise
 */
public interface BotNameChangedEvent
    extends Event, BotObject {
    
    /**
     * 获取修改前的昵称
     *
     * @return 修改前的昵称
     */
    String getPreviousName();
    
    /**
     * 获取修改后的昵称
     *
     * @return 修改后的昵称
     */
    String getCurrentName();
}
