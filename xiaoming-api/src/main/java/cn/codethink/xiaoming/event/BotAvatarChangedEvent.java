package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.BotObject;

/**
 * Bot 修改头像事件
 *
 * @author Chuanwise
 */
public interface BotAvatarChangedEvent
    extends Event, BotObject {
    
    /**
     * 获取修改前的头像 url
     *
     * @return 修改前的头像 url
     */
    String getPreviousAvatarUrl();
    
    /**
     * 获取修改后的头像 url
     *
     * @return 修改后的头像 url
     */
    String getCurrentAvatarUrl();
}
