package cn.chuanwise.xiaoming.core.recept;

import cn.chuanwise.utility.ArgumentUtility;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.StringUtility;
import cn.chuanwise.utility.TimeUtility;
import cn.chuanwise.xiaoming.api.annotation.EventHandler;
import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.configuration.Configuration;
import cn.chuanwise.xiaoming.api.limit.CallLimitConfig;
import cn.chuanwise.xiaoming.api.limit.UserCallLimiter;
import cn.chuanwise.xiaoming.api.permission.PermissionAccessible;
import cn.chuanwise.xiaoming.api.recept.Receptionist;
import cn.chuanwise.xiaoming.api.recept.ReceptionistManager;
import cn.chuanwise.xiaoming.core.event.EventListenerImpl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import org.slf4j.Logger;

import java.beans.Transient;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Chuanwise
 */
@Slf4j
@Getter
public class ReceptionistManagerImpl extends EventListenerImpl implements ReceptionistManager {
    @Override
    @Transient
    public Logger getLog() {
        return log;
    }

    public ReceptionistManagerImpl(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
    }

    /**
     * 用户接待员记录器
     */
    Map<Long, Receptionist> receptionists = new ConcurrentHashMap<>();
    Receptionist botReceptionist;

    @Override
    public Receptionist getBotReceptionist() {
        if (Objects.isNull(botReceptionist)) {
            botReceptionist = forReceptionist(getXiaomingBot().getMiraiBot().getId());
        }
        return botReceptionist;
    }

    @Override
    public Receptionist forReceptionist(long qq) {
        return CollectionUtility.getOrSupplie(receptionists, qq, () -> new ReceptionistImpl(getXiaomingBot(), qq));
    }

    public boolean callable(Contact contact, boolean inGroup) {
        final long qq = contact.getId();
        final UserCallLimiter limiter;
        if (inGroup) {
            limiter = getXiaomingBot().getUserCallLimitManager().getGroupCallLimiter();
        } else {
            limiter = getXiaomingBot().getUserCallLimitManager().getPrivateCallLimiter();
        }
        if (limiter.uncallable(qq)) {
            if (limiter.isTooManySoUncallable(qq) && limiter.shouldNotice(qq)) {
                if (getXiaomingBot().getPermissionManager().userAccessible(qq, "limit.bypass") != PermissionAccessible.ACCESSABLE) {
                    final CallLimitConfig config = limiter.getConfig();
                    final String sceneName = inGroup ? "群聊" : "私聊";
                    contact.sendMessage(
                            ArgumentUtility.replaceArguments("你在{}{}内召唤了{}次小明啦，好好休息一下，等{}咱们再一起在{}玩吧 {}",
                                    new Object[]{
                                            sceneName,
                                            TimeUtility.toTimeLength(config.getPeriod()),
                                            config.getTop(),
                                            TimeUtility.after(config.getPeriod()),
                                            sceneName,
                                            getXiaomingBot().getLanguage().get("happy")
                                    })
                    );

                    limiter.setNoticed(qq);
                    final Receptionist receptionist = forReceptionist(qq);
                    if (Objects.nonNull(receptionist)) {
                        receptionist.stop();
                    }
                    return false;
                } else {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    @EventHandler
    public void onGroupMessageEvent(GroupMessageEvent event) {
        final Member member = event.getSender();
        final long qq = member.getId();

        final String message = event.getMessage().serializeToMiraiCode();
        final Configuration configuration = getXiaomingBot().getConfiguration();

        // 如果本群是启动明确调用的群
        // 检查明确调用
        String callContent = null;
        if (configuration.isEnableClearCall() && getXiaomingBot().getGroupRecordManager().hasTag(event.getGroup().getId(), configuration.getClearCallGroupTag())) {
            for (String prefix : configuration.getClearCallPrefixes()) {
                if (message.startsWith(prefix) && message.length() > prefix.length()) {
                    callContent = message.substring(prefix.length());
                }
            }
        } else {
            callContent = message;
        }
        if (Objects.nonNull(callContent)) {
            callContent = callContent.trim();
        }
        if (StringUtility.isEmpty(callContent)) {
            return;
        }

        if (!callable(member, true)) {
            getLog().warn("小明收到了来自 " + qq + " 的群聊消息：" + event.getMessage().serializeToMiraiCode() + "，但因为其尚处在调用限制期，故忽略此消息。");
            return;
        }

        final Receptionist receptionist = forReceptionist(qq);

        final Group group = event.getGroup();
        receptionist.onGroupMessage(getXiaomingBot().getContactManager().getGroupContact(group.getId()), callContent, event.getMessage());
    }

    @Override
    @EventHandler
    public void onPrivateMessageEvent(FriendMessageEvent event) {
        final Friend friend = event.getFriend();

        final long qq = friend.getId();
        if (!callable(friend, false)) {
            getLog().warn("小明收到了来自 " + qq + " 的私聊消息：" + event.getMessage().serializeToMiraiCode() + "，但因为其尚处在调用限制期，故忽略此消息。");
            return;
        }

        final Receptionist receptionist = forReceptionist(qq);
        receptionist.onPrivateMessage(getXiaomingBot().getContactManager().getPrivateContact(qq), event.getMessage());
    }

    @Override
    @EventHandler
    public void onMemberMessageEvent(GroupTempMessageEvent event) {
        final Group group = event.getGroup();
        final NormalMember member = event.getSender();

        final long qq = member.getId();
        if (!callable(member, false)) {
            getLog().warn("小明收到了来自 " + qq + " 的临时会话消息：" + event.getMessage().serializeToMiraiCode() + "，但因为其尚处在调用限制期，故忽略此消息。");
            return;
        }

        final Receptionist receptionist = forReceptionist(qq);
        receptionist.onMemberMessage(getXiaomingBot().getContactManager().getMemberContact(group.getId(), qq), event.getMessage());
    }
}
