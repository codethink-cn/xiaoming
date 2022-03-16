package cn.codethink.xiaoming;

import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.configuration.CoreConfiguration;
import cn.codethink.xiaoming.contact.Friend;
import cn.codethink.xiaoming.contact.Scope;
import cn.codethink.xiaoming.event.EventManager;
import cn.codethink.xiaoming.logger.Logger;

import java.util.List;

/**
 * 小明核心 API
 *
 * @author Chuanwise
 */
public interface Bot {
    
    /**
     * 获取当前小明状态
     *
     * @return 当前小明状态
     */
    State getState();
    
    /**
     * 获取核心配置项
     *
     * @return 核心配置项
     */
    CoreConfiguration getCoreConfiguration();
    
    /**
     * 设置核心配置项
     *
     * @param coreConfiguration 核心配置项
     */
    void setCoreConfiguration(CoreConfiguration coreConfiguration);
    
    /**
     * 启动机器人
     */
    void start();
    
    /**
     * 关闭机器人
     */
    void stop();
    
    /**
     * 获得和自己的私聊
     *
     * @return 和自己的私聊
     */
    Friend getSelf();
    
    /**
     * 获取好友列表
     *
     * @return 好友列表
     */
    List<Friend> getFriends();
    
    /**
     * 获取指定的好友
     *
     * @param code 好友标识
     * @return 当找到该好友，返回该好友，否则返回 null
     */
    Friend getFriend(Code code);
    
    /**
     * 获取范围列表
     *
     * @return 范围列表
     */
    List<Scope> getScopes();
    
    /**
     * 获取某个范围
     *
     * @param code 范围标识码
     * @return 当找到该范围，返回该范围，否则返回 null
     */
    Scope getScope(Code code);
    
    /**
     * 获取日志记录器
     *
     * @return 日志记录器
     */
    Logger getLogger();
    
    /**
     * 获取事件管理器
     *
     * @return 事件管理器
     */
    EventManager getEventManager();
}
