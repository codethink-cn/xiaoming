package cn.codethink.xiaoming.contact;

import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.util.CachedContactMap;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.contact.Stranger;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.*;

import java.util.Collections;
import java.util.Map;

/**
 * 会话管理器，负责维护会话列表。
 *
 * @author Chuanwise
 */
public class MiraiContactManager
    extends AbstractBotObject {
    
    /**
     * 群聊实例缓存
     */
    private final CachedContactMap<Group, MiraiGroup> groups = new CachedContactMap<>(
        group -> new MiraiGroup((MiraiBot) bot, group),
        miraiGroup -> miraiGroup.available = true,
        miraiGroup -> miraiGroup.available = false,
        group -> Code.ofLong(group.getId())
    );
    
    /**
     * 好友实例缓存
     */
    private final CachedContactMap<Friend, MiraiFriend> friends = new CachedContactMap<>(
        friend -> new MiraiFriend((MiraiBot) bot, friend),
        miraiFriend -> miraiFriend.available = true,
        miraiFriend -> miraiFriend.available = false,
        friend -> Code.ofLong(friend.getId())
    );
    
    /**
     * 陌生人实例缓存
     */
    private final CachedContactMap<Stranger, MiraiStranger> strangers = new CachedContactMap<>(
        stranger -> new MiraiStranger((MiraiBot) bot, stranger),
        miraiStranger -> miraiStranger.available = true,
        miraiStranger -> miraiStranger.available = false,
        stranger -> Code.ofLong(stranger.getId())
    );
    
    private final MiraiFriend botAsFriend;
    
    private final MiraiStranger botAsStranger;
    
    public MiraiContactManager(Bot bot) {
        super(bot);
    
        // register contact update listener host
        final net.mamoe.mirai.Bot miraiBot = ((MiraiBot) bot).getMiraiBot();
        miraiBot.getEventChannel().registerListenerHost(new CacheUpdater());
    
        // friends
        final Map<Code, MiraiFriend> friendsAvailable = friends.getAvailable();
        for (Friend friend : miraiBot.getFriends()) {
            friendsAvailable.put(Code.ofLong(friend.getId()), new MiraiFriend((MiraiBot) bot, friend));
        }
        // bot as friend
        this.botAsFriend = new MiraiFriend((MiraiBot) bot, miraiBot.getAsFriend());
        friendsAvailable.put(bot.getCode(), botAsFriend);
    
        // groups
        final Map<Code, MiraiGroup> groupsAvailable = groups.getAvailable();
        for (Group group : miraiBot.getGroups()) {
            groupsAvailable.put(Code.ofLong(group.getId()), new MiraiGroup((MiraiBot) bot, group));
        }
    
        // strangers
        final Map<Code, MiraiStranger> strangersAvailable = strangers.getAvailable();
        for (Stranger stranger : miraiBot.getStrangers()) {
            strangersAvailable.put(Code.ofLong(stranger.getId()), new MiraiStranger((MiraiBot) bot, stranger));
        }
        // bot as stranger
        this.botAsStranger = new MiraiStranger((MiraiBot) bot, miraiBot.getAsStranger());
        strangersAvailable.put(bot.getCode(), botAsStranger);
    }
    
    /**
     * 缓存更新器，是 Mirai 机器人的事件监听器
     */
    private class CacheUpdater
        implements ListenerHost {
    
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
        public void onFriendAdd(FriendAddEvent event) {
            friends.getAvailable(event.getFriend());
        }
    
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
        public void onFriendDelete(FriendDeleteEvent event) {
            friends.getUnavailable(event.getFriend());
        }
    
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
        public void onBotJoinGroup(BotJoinGroupEvent event) {
            groups.getAvailable(event.getGroup());
        }
    
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
        public void onBotQuitGroup(BotLeaveEvent event) {
            groups.getUnavailable(event.getGroup());
        }
    
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
        public void onMemberJoinGroup(MemberJoinEvent event) {
            final MiraiGroup miraiGroup = groups.getAvailable(event.getGroup());
            miraiGroup.members.getAvailable(event.getMember());
        }
    
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
        public void onMemberLeaveGroup(MemberLeaveEvent event) {
            final MiraiGroup miraiGroup = groups.getAvailable(event.getGroup());
            miraiGroup.members.getUnavailable((NormalMember) event.getMember());
        }
    }
    
    public MiraiFriend getBotAsFriend() {
        return botAsFriend;
    }
    
    public MiraiStranger getBotAsStranger() {
        return botAsStranger;
    }
    
    public Map<Code, MiraiGroup> getGroups() {
        return Collections.unmodifiableMap(groups.getAvailable());
    }
    
    public MiraiGroup getGroup(Code code) {
        return groups.getAvailable(code);
    }
    
    public Map<Code, MiraiFriend> getFriends() {
        return Collections.unmodifiableMap(friends.getAvailable());
    }
    
    public MiraiFriend getFriend(Code code) {
        return friends.getAvailable(code);
    }
    
    public MiraiStranger getStranger(Code code) {
        return strangers.getAvailable(code);
    }
    
    public Map<Code, MiraiStranger> getStrangers() {
        return Collections.unmodifiableMap(strangers.getAvailable());
    }
}
