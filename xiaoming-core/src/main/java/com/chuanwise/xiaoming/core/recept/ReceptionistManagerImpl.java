package com.chuanwise.xiaoming.core.recept;

import com.chuanwise.xiaoming.api.annotation.EventHandler;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.limit.CallLimitConfig;
import com.chuanwise.xiaoming.api.limit.UserCallLimiter;
import com.chuanwise.xiaoming.api.permission.PermissionAccessible;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.recept.ReceptionistManager;
import com.chuanwise.xiaoming.api.util.ArgumentUtils;
import com.chuanwise.xiaoming.api.util.TimeUtils;
import com.chuanwise.xiaoming.core.event.EventListenerImpl;
import com.chuanwise.xiaoming.core.user.XiaomingUserImpl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Chuanwise
 */
@Slf4j
public class ReceptionistManagerImpl extends EventListenerImpl implements ReceptionistManager {
    @Override
    public Logger getLog() {
        return log;
    }

    public ReceptionistManagerImpl(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
    }

    /**
     * 用户接待员记录器
     */
    @Getter
    Map<Long, Receptionist> receptionists = new ConcurrentHashMap<>();

    @Override
    public Receptionist getOrPutReceptionist(long qq) {
        Receptionist receptionist = getReceptionist(qq);
        if (Objects.isNull(receptionist)) {
            receptionist = new ReceptionistImpl(getXiaomingBot(), qq);
            receptionists.put(qq, receptionist);
        }
        return receptionist;
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
                            ArgumentUtils.replaceArguments("你在{}{}内召唤了{}次小明啦，好好休息一下，等{}咱们再一起在{}玩吧 {}",
                                    new Object[]{
                                            sceneName,
                                            TimeUtils.toTimeString(config.getPeriod()),
                                            config.getTop(),
                                            TimeUtils.after(config.getPeriod()),
                                            sceneName,
                                            getXiaomingBot().getLanguageManager().get("happy")
                                    })
                    );

                    limiter.setNoticed(qq);
                    final Receptionist receptionist = getReceptionist(qq);
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

        // 检查调用频率
        if (!callable(member, true)) {
            getLog().warn("小明收到了来自 " + qq + " 的群聊消息：" + event.getMessage().serializeToMiraiCode() + "，但因为其尚处在调用限制期，故忽略此消息。");
            return;
        }
        final Receptionist receptionist = getOrPutReceptionist(qq);

        final Group group = event.getGroup();
        receptionist.onGroupMessage(getXiaomingBot().getContactManager().getGroupContact(group.getId()), event.getMessage());
    }

    @Override
    @EventHandler
    public void onPrivateMessageEvent(FriendMessageEvent event) {
        final Friend friend = event.getFriend();

        final long qq = friend.getId();

        // 检查调用频率
        if (!callable(friend, false)) {
            getLog().warn("小明收到了来自 " + qq + " 的私聊消息：" + event.getMessage().serializeToMiraiCode() + "，但因为其尚处在调用限制期，故忽略此消息。");
            return;
        }
        final Receptionist receptionist = getOrPutReceptionist(qq);
        receptionist.onPrivateMessage(getXiaomingBot().getContactManager().getPrivateContact(qq), event.getMessage());
    }

    @Override
    @EventHandler
    public void onTempMessageEvent(GroupTempMessageEvent event) {
        final Group group = event.getGroup();
        final NormalMember member = event.getSender();

        final long qq = member.getId();
        if (!callable(member, false)) {
            getLog().warn("小明收到了来自 " + qq + " 的临时会话消息：" + event.getMessage().serializeToMiraiCode() + "，但因为其尚处在调用限制期，故忽略此消息。");
            return;
        }
        final Receptionist receptionist = getOrPutReceptionist(qq);
        receptionist.onTempMessage(getXiaomingBot().getContactManager().getTempContact(group.getId(), qq), event.getMessage());
    }
}
