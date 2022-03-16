package cn.codethink.xiaoming;

import cn.codethink.util.Preconditions;
import cn.codethink.xiaoming.annotation.Internal;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.code.LongCode;
import cn.codethink.xiaoming.concurrent.ThreadPoolScheduler;
import cn.codethink.xiaoming.contact.Friend;
import cn.codethink.xiaoming.contact.MiraiFriend;
import cn.codethink.xiaoming.contact.MiraiGroup;
import cn.codethink.xiaoming.contact.Scope;
import cn.codethink.xiaoming.event.BotStartEvent;
import cn.codethink.xiaoming.event.BotStopEvent;
import cn.codethink.xiaoming.event.EventForwarder;
import cn.codethink.xiaoming.event.SimpleEventManager;
import cn.codethink.xiaoming.exception.BotStopException;
import cn.codethink.xiaoming.logger.LoggerFactory;
import cn.codethink.xiaoming.message.ResourcePool;
import cn.codethink.xiaoming.util.Codes;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.utils.MiraiLogger;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Chuanwise
 */
@Internal
@Data
public class MiraiBot
        extends AbstractBot {
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    protected final boolean adapter;
    
    protected Bot miraiBot;
    
    protected MiraiBot(Bot miraiBot, boolean adapter) {
        Preconditions.namedArgumentNonNull(miraiBot, "mirai bot");
        
        this.miraiBot = miraiBot;
        this.adapter = adapter;
    }
    
    @Override
    public void start() {
        Preconditions.state(state == State.IDLE
            || state == State.START_ERROR);
        
        try {
            state = State.STARTING;
    
            // 设置核心线程池
            scheduler = new ThreadPoolScheduler(this, coreConfiguration.getThreadCount());
    
            // 登录
            if (!miraiBot.isOnline()) {
                miraiBot.login();
            }
    
            // 设置资源池
            resourcePool = new ResourcePool(this);
            
            // 设置日志记录器
            logger = LoggerFactory.of("XiaoMing");
            
            // 设置事件管理器
            eventManager = new SimpleEventManager(this);
            
            // 注册转发器
            miraiBot.getEventChannel().registerListenerHost(new EventForwarder(this));
    
            state = State.STARTED;
    
            // 发出事件
            final BotStartEvent botStartEvent = new BotStartEvent(this);
            eventManager.handleEvent(botStartEvent);
    
        } catch (Throwable throwable) {
            state = State.START_ERROR;
            
            throw new BotStopException(this, throwable);
        }
    }
    
    @Override
    public void stop() {
        Preconditions.state(state == State.STARTED
            || state == State.STOP_ERROR);
        
        try {
            state = State.STOPPING;
            
            // 关闭线程池
            scheduler.shutdownGracefully();
            scheduler = null;
            
            // 清空资源池
            resourcePool = null;
            
            // 关闭日志记录器
            logger = null;
            
            // 关闭事件管理器
            eventManager = null;
            
            state = State.IDLE;
    
            // 发出事件
            final BotStopEvent botStopEvent = new BotStopEvent(this);
            eventManager.handleEvent(botStopEvent);
    
            // 关闭 Bot
            if (!adapter) {
                miraiBot.close();
            }
        } catch (Throwable throwable) {
            state = State.STOP_ERROR;
            
            throw new BotStopException(this, throwable);
        }
    }
    
    @Override
    public Friend getSelf() {
        return new MiraiFriend(this, miraiBot.getAsFriend());
    }
    
    @Override
    public List<Friend> getFriends() {
        return Collections.unmodifiableList(
            miraiBot.getFriends()
                .stream()
                .map(x -> new MiraiFriend(MiraiBot.this, x))
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public Friend getFriend(Code code) {
        Codes.requiredLongCode(code);
    
        final net.mamoe.mirai.contact.Friend friend = miraiBot.getFriend(((LongCode) code).getCode());
        if (Objects.nonNull(friend)) {
            return new MiraiFriend(this, friend);
        }
        
        return null;
    }
    
    @Override
    public List<Scope> getScopes() {
        return Collections.unmodifiableList(
            miraiBot.getGroups()
                .stream()
                .map(group -> new MiraiGroup(MiraiBot.this, group))
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public Scope getScope(Code code) {
        Codes.requiredLongCode(code);
        
        final long scopeCode = ((LongCode) code).getCode();
        final Group group = miraiBot.getGroup(scopeCode);
        
        if (Objects.isNull(group)) {
            return null;
        } else {
            return new MiraiGroup(this, group);
        }
    }
}
