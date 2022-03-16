package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.event.events.BotAvatarChangedEvent;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.BotReloginEvent;
import net.mamoe.mirai.message.action.BotNudge;

/**
 * 事件转发器
 *
 * @author Chuanwise
 */
public class EventForwarder
        extends AbstractBotObject
        implements ListenerHost {
    
    private volatile String avatarUrl;
    
    public EventForwarder(Bot bot) {
        super(bot);
        
        avatarUrl = bot.getSelf().getAvatarUrl();
    }
    
    @EventHandler
    public void onBotOnline(BotOnlineEvent event) {
        final Object newEvent = new cn.codethink.xiaoming.event.BotOnlineEvent(bot);
        bot.getEventManager().handleEvent(newEvent);
    }
    
    @EventHandler
    public void onBotOffline(BotOfflineEvent event) {
        final Object newEvent = new cn.codethink.xiaoming.event.BotOfflineEvent(bot, event.getReconnect());
        bot.getEventManager().handleEvent(newEvent);
    }
    
    @EventHandler
    public void onBotRelogin(BotReloginEvent event) {
        final Object newEvent = new cn.codethink.xiaoming.event.BotReloginEvent(bot, event.getCause());
        bot.getEventManager().handleEvent(newEvent);
    }
    
    @EventHandler
    public void onBotAvatarChanged(BotAvatarChangedEvent event) {
        final String previousAvatarUrl = avatarUrl;
        final String currentAvatarUrl = bot.getSelf().getAvatarUrl();
        this.avatarUrl = currentAvatarUrl;
        
        final Object newEvent = new cn.codethink.xiaoming.event.BotAvatarChangedEvent(
            bot,
            previousAvatarUrl,
            currentAvatarUrl
        );
        bot.getEventManager().handleEvent(newEvent);
    }
    
    @EventHandler
    public void onBotNickChanged(BotNickChangedEvent event) {
        final Object newEvent = new BotNameChangedEvent(bot, event.getFrom(), event.getTo());
        bot.getEventManager().handleEvent(newEvent);
    }
}
