package cn.codethink.xiaoming;

import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.configuration.BotConfiguration;
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
     * 平台版本
     */
    String VERSION = "5.0";
    
    /**
     * 版本分支
     */
    VersionBranch VERSION_BRANCH = VersionBranch.SNAPSHOT;
    
    /**
     * 开发团队 ID
     */
    String GROUP = "CodeThink";
    
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
    BotConfiguration getBotConfiguration();
    
    /**
     * 设置核心配置项
     *
     * @param botConfiguration 核心配置项
     */
    void setBotConfiguration(BotConfiguration botConfiguration);
    
    /**
     * 启动机器人
     */
    boolean start();
    
    /**
     * 关闭机器人
     */
    boolean stop();
    
    /**
     * 判断机器人是否启动
     *
     * @return 机器人是否启动
     */
    boolean isStarted();
    
    /**
     * 判断机器人是否关闭
     *
     * @return 机器人是否关闭
     */
    boolean isStopped();
    
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
    
    /**
     * 获取实现软件类型
     *
     * @return
     */
    InstantMessenger getInstantMessenger();
}
