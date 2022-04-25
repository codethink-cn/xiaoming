package cn.codethink.xiaoming.contact;

import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.QqBot;
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
public class QqContactManager
    extends AbstractBotObject {
    
    /**
     * 群聊实例缓存
     */
    private final CachedContactMap<Group, QqGroup> groups = new CachedContactMap<>(
        group -> new QqGroup((QqBot) bot, group),
        qqGroup -> qqGroup.available = true,
        qqGroup -> qqGroup.available = false,
        group -> Code.ofLong(group.getId())
    );
    
    /**
     * 好友实例缓存
     */
    private final CachedContactMap<Friend, QqFriend> friends = new CachedContactMap<>(
        friend -> new QqFriend((QqBot) bot, friend),
        qqFriend -> qqFriend.available = true,
        qqFriend -> qqFriend.available = false,
        friend -> Code.ofLong(friend.getId())
    );
    
    /**
     * 陌生人实例缓存
     */
    private final CachedContactMap<Stranger, QqStranger> strangers = new CachedContactMap<>(
        stranger -> new QqStranger((QqBot) bot, stranger),
        qqStranger -> qqStranger.available = true,
        qqStranger -> qqStranger.available = false,
        stranger -> Code.ofLong(stranger.getId())
    );
    
    private final QqFriend botAsFriend;
    
    private final QqStranger botAsStranger;
    
    public QqContactManager(Bot bot) {
        super(bot);
    
        // register contact update listener host
        final net.mamoe.mirai.Bot qqBot = ((QqBot) bot).getQqBot();
        qqBot.getEventChannel().registerListenerHost(new CacheUpdater());
    
        // friends
        final Map<Code, QqFriend> friendsAvailable = friends.getAvailable();
        for (Friend friend : qqBot.getFriends()) {
            friendsAvailable.put(Code.ofLong(friend.getId()), new QqFriend((QqBot) bot, friend));
        }
        // bot as friend
        this.botAsFriend = new QqFriend((QqBot) bot, qqBot.getAsFriend());
        friendsAvailable.put(bot.getCode(), botAsFriend);
    
        // groups
        final Map<Code, QqGroup> groupsAvailable = groups.getAvailable();
        for (Group group : qqBot.getGroups()) {
            groupsAvailable.put(Code.ofLong(group.getId()), new QqGroup((QqBot) bot, group));
        }
    
        // strangers
        final Map<Code, QqStranger> strangersAvailable = strangers.getAvailable();
        for (Stranger stranger : qqBot.getStrangers()) {
            strangersAvailable.put(Code.ofLong(stranger.getId()), new QqStranger((QqBot) bot, stranger));
        }
        // bot as stranger
        this.botAsStranger = new QqStranger((QqBot) bot, qqBot.getAsStranger());
        strangersAvailable.put(bot.getCode(), botAsStranger);
    }
    
    /**
     * 缓存更新器，是 Qq 机器人的事件监听器
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
            final QqGroup qqGroup = groups.getAvailable(event.getGroup());
            qqGroup.members.getAvailable(event.getMember());
        }
    
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
        public void onMemberLeaveGroup(MemberLeaveEvent event) {
            final QqGroup qqGroup = groups.getAvailable(event.getGroup());
            qqGroup.members.getUnavailable((NormalMember) event.getMember());
        }
    }
    
    public QqFriend getBotAsFriend() {
        return botAsFriend;
    }
    
    public QqStranger getBotAsStranger() {
        return botAsStranger;
    }
    
    public Map<Code, QqGroup> getGroups() {
        return Collections.unmodifiableMap(groups.getAvailable());
    }
    
    public QqGroup getGroup(Code code) {
        return groups.getAvailable(code);
    }
    
    public Map<Code, QqFriend> getFriends() {
        return Collections.unmodifiableMap(friends.getAvailable());
    }
    
    public QqFriend getFriend(Code code) {
        return friends.getAvailable(code);
    }
    
    public QqStranger getStranger(Code code) {
        return strangers.getAvailable(code);
    }
    
    public Map<Code, QqStranger> getStrangers() {
        return Collections.unmodifiableMap(strangers.getAvailable());
    }
}
