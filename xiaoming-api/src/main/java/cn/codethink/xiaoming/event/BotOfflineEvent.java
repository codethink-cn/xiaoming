package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.BotObject;

/**
 * Bot 离线事件
 *
 * @author Chuanwise
 */
public interface BotOfflineEvent
    extends Event, BotObject {
    
    /**
     * 询问是否需要重新登录
     *
     * @return 是否需要重新登录
     */
    boolean isRelogin();
}
