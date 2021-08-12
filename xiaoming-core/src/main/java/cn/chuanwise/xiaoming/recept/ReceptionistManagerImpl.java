package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.toolkit.sized.SizedResidentConcurrentHashMap;
import cn.chuanwise.utility.*;
import cn.chuanwise.xiaoming.annotation.EventHandler;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.limit.CallLimitConfiguration;
import cn.chuanwise.xiaoming.limit.CallLimiter;
import cn.chuanwise.xiaoming.permission.PermissionAccessible;
import cn.chuanwise.xiaoming.event.EventListenerImpl;
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

/**
 * @author Chuanwise
 */
@Slf4j
@Getter
public class ReceptionistManagerImpl extends EventListenerImpl implements ReceptionistManager {
    @Override
    @Transient
    public Logger getLogger() {
        return log;
    }

    public ReceptionistManagerImpl(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        this.receptionists = new SizedResidentConcurrentHashMap<>(xiaomingBot.getConfiguration().getMaxReceptionistQuantity());
    }

    /**
     * 用户接待员记录器
     */
    final Map<Long, Receptionist> receptionists;
    Receptionist botReceptionist;

    @Override
    public Receptionist getBotReceptionist() {
        if (Objects.isNull(botReceptionist)) {
            botReceptionist = forReceptionist(getXiaomingBot().getMiraiBot().getId());
        }
        return botReceptionist;
    }

    @Override
    public Receptionist forReceptionist(long code) {
        return MapUtility.getOrPutSupply(receptionists, code, () -> new ReceptionistImpl(getXiaomingBot(), code));
    }

    public boolean callable(Contact contact, boolean inGroup) {
        final long qq = contact.getId();
        final CallLimiter limiter;
        if (inGroup) {
            limiter = getXiaomingBot().getUserCallLimitManager().getGroupCallLimiter();
        } else {
            limiter = getXiaomingBot().getUserCallLimitManager().getPrivateCallLimiter();
        }
        if (limiter.uncallable(qq)) {
            if (limiter.isTooManySoUncallable(qq) && limiter.shouldNotice(qq)) {
                if (getXiaomingBot().getPermissionManager().userAccessible(qq, "limit.bypass") != PermissionAccessible.ACCESSABLE) {
                    final CallLimitConfiguration config = limiter.getConfiguration();
                    final String sceneName = inGroup ? "群聊" : "私聊";
                    contact.sendMessage(
                            ArgumentUtility.render("你在{}{}内召唤了{}次小明啦，好好休息一下，等{}咱们再一起在{}玩吧 {}",
                                    new Object[]{
                                            sceneName,
                                            TimeUtility.toTimeLength(config.getPeriod()),
                                            config.getTop(),
                                            TimeUtility.after(config.getPeriod()),
                                            sceneName,
//                                            getXiaomingBot().getLanguage().get("happy")
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
            getLogger().warn("小明收到了来自 " + qq + " 的群聊消息：" + event.getMessage().serializeToMiraiCode() + "，但因为其尚处在调用限制期，故忽略此消息。");
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
            getLogger().warn("小明收到了来自 " + qq + " 的私聊消息：" + event.getMessage().serializeToMiraiCode() + "，但因为其尚处在调用限制期，故忽略此消息。");
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
            getLogger().warn("小明收到了来自 " + qq + " 的临时会话消息：" + event.getMessage().serializeToMiraiCode() + "，但因为其尚处在调用限制期，故忽略此消息。");
            return;
        }

        final Receptionist receptionist = forReceptionist(qq);
        receptionist.onMemberMessage(getXiaomingBot().getContactManager().getMemberContact(group.getId(), qq), event.getMessage());
    }
}
