package cn.codethink.xiaoming;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.configuration.BotConfiguration;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.event.EventManager;
import cn.codethink.xiaoming.exception.*;
import cn.codethink.xiaoming.logger.Logger;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 小明核心 API
 *
 * @author Chuanwise
 */
public interface Bot
    extends Sender, UserOrBot, ContactOrBot {
    
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
     * 获取好友列表
     *
     * @return 好友列表
     */
    Map<Code, ? extends Friend> getFriends();
    
    /**
     * 获取指定的好友
     *
     * @param code 好友标识
     * @return 当找到该好友，返回该好友，否则返回 null
     */
    Friend getFriend(Code code);
    
    /**
     * 获取指定的好友
     *
     * @param code 好友账户码
     * @return 好友
     * @throws cn.codethink.xiaoming.exception.NoSuchFriendException 找不到该好友时
     * @throws NullPointerException code 为 null 时
     */
    default Friend getFriendOrFail(Code code) {
        Preconditions.objectNonNull(code, "code");
    
        final Friend friend = getFriend(code);
        if (Objects.isNull(friend)) {
            throw new NoSuchFriendException(this, code);
        }
        
        return friend;
    }
    
    /**
     * 获取陌生人表
     *
     * @return 陌生人表
     */
    Map<Code, ? extends Stranger> getStrangers();
    
    /**
     * 获取陌生人
     *
     * @param code 账号码
     * @return 当存在该陌生人，返回陌生人，否则返回 null
     */
    Stranger getStranger(Code code);
    
    /**
     * 获取指定的陌生人
     *
     * @param code 陌生人账户码
     * @return 陌生人
     * @throws cn.codethink.xiaoming.exception.NoSuchStrangerException 找不到该陌生人时
     * @throws NullPointerException code 为 null 时
     */
    default Stranger getStrangerOrFail(Code code) {
        Preconditions.objectNonNull(code, "code");
    
        final Stranger stranger = getStranger(code);
        if (Objects.isNull(stranger)) {
            throw new NoSuchStrangerException(this, code);
        }
        
        return stranger;
    }
    
    /**
     * 获取范围表
     *
     * @return 范围列表
     */
    Map<Code, ? extends Mass> getMasses();
    
    /**
     * 获取某个范围
     *
     * @param code 范围标识码
     * @return 当找到该范围，返回该范围，否则返回 null
     */
    Mass getMass(Code code);
    
    /**
     * 获取指定的集体
     *
     * @param code 集体码
     * @return 集体
     * @throws cn.codethink.xiaoming.exception.NoSuchStrangerException 找不到该集体
     * @throws NullPointerException code 为 null
     */
    default Mass getMassOrFail(Code code) {
        Preconditions.objectNonNull(code, "code");
        
        final Mass mass = getMass(code);
        if (Objects.isNull(mass)) {
            throw new NoSuchMassException(this, code);
        }
        
        return mass;
    }
    
    /**
     * 获得指定的群聊
     *
     * @param code 群号
     * @return 群聊，或 null
     * @throws NullPointerException code 为 null
     */
    default Group getGroup(Code code) {
        Preconditions.objectNonNull(code, "code");
    
        final Mass mass = getMass(code);
        if (mass instanceof Group) {
            return (Group) mass;
        }
        
        return null;
    }
    
    /**
     * 获取指定的群聊
     *
     * @param code 群号
     * @return 群聊
     * @throws cn.codethink.xiaoming.exception.NoSuchStrangerException 找不到该群聊，或该集体不是群聊
     * @throws NullPointerException code 为 null
     */
    default Group getGroupOrFail(Code code) {
        Preconditions.objectNonNull(code, "code");
    
        final Group group = getGroup(code);
        if (Objects.isNull(group)) {
            throw new NoSuchGroupException(this, code);
        }
        
        return group;
    }
    
    /**
     * 获取所有的群聊
     *
     * @return 群聊
     */
    @SuppressWarnings("all")
    default Map<Code, ? extends Group> getGroups() {
        return Collections.unmodifiableMap(
            (Map<Code, ? extends Group>) getMasses().entrySet()
                .stream()
                .filter(x -> x.getValue() instanceof Group)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }
    
    /**
     * 获得指定的行会
     *
     * @param code 行会号
     * @return 行会，或 null
     * @throws NullPointerException code 为 null
     */
    default Guild getGuild(Code code) {
        Preconditions.objectNonNull(code, "code");
    
        final Mass mass = getMass(code);
        if (mass instanceof Guild) {
            return (Guild) mass;
        }
        
        return null;
    }
    
    /**
     * 获取指定的行会
     *
     * @param code 行会号
     * @return 行会
     * @throws cn.codethink.xiaoming.exception.NoSuchStrangerException 找不到该行会，或该集体不是行会
     * @throws NullPointerException code 为 null
     */
    default Guild getGuildOrFail(Code code) {
        Preconditions.objectNonNull(code, "code");
        
        final Guild guild = getGuild(code);
        if (Objects.isNull(guild)) {
            throw new NoSuchGuildException(this, code);
        }
        
        return guild;
    }
    
    /**
     * 获取所有的行会
     *
     * @return 行会
     */
    @SuppressWarnings("all")
    default Map<Code, ? extends Guild> getGuilds() {
        return Collections.unmodifiableMap(
            (Map<Code, ? extends Guild>) getMasses().entrySet()
                .stream()
                .filter(x -> x.getValue() instanceof Guild)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }
    
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
     * 获取即时通讯软件类型
     *
     * @return 即时通讯软件类型
     */
    IM getIM();
    
    /**
     * 获得 QQ 号和 Bot 相同的 {@link Friend} 实例
     *
     * @return QQ 号和 Bot 相同的 {@link Friend} 实例
     */
    @Override
    Friend asFriend();
    
    /**
     * 获得 QQ 号和 Bot 相同的 {@link Stranger} 实例
     *
     * @return QQ 号和 Bot 相同的 {@link Stranger} 实例
     */
    @Override
    Stranger asStranger();
}
