package com.chuanwise.xiaoming.core.recept;

import com.chuanwise.xiaoming.api.annotation.EventHandler;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.user.Receptionist;
import com.chuanwise.xiaoming.api.user.ReceptionistManager;
import com.chuanwise.xiaoming.core.event.EventListenerImpl;
import com.chuanwise.xiaoming.core.user.XiaomingUserImpl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;
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

    public Receptionist getOrPutReceptionist(long qq) {
        Receptionist receptionist = getReceptionist(qq);
        if (Objects.isNull(receptionist)) {
            receptionist = new ReceptionistImpl(new XiaomingUserImpl(getXiaomingBot()));
            receptionists.put(qq, receptionist);
        }
        return receptionist;
    }

    @Override
    @EventHandler
    public void onGroupMessageEvent(GroupMessageEvent event) {
        final Group group = event.getGroup();
        final Member member = event.getSender();
        final ResponseGroup responseGroup = getXiaomingBot().getResponseGroupManager().forCode(group.getId());

        // 查找该用户的接待员
        final long qq = member.getId();
        final Receptionist receptionist = getOrPutReceptionist(qq);

        final ReceptionTask groupTask = receptionist.getGroupTask(group.getId());
        final ReceptionTask externalTask = receptionist.getExternalTask();

        // 不是响应群就算了
        if (Objects.isNull(responseGroup)) {
            if (Objects.nonNull(groupTask)) {
                groupTask.optimize();
            }
            receptionist.getOrPutExternalTask(member).onMessage(event.getMessage().serializeToMiraiCode());
        } else {
            if (Objects.nonNull(externalTask) && externalTask.getMember().getGroup().getId() == group.getId()) {
                externalTask.optimize();
            }
            receptionist.getOrPutGroupTask(responseGroup, member).onMessage(event.getMessage().serializeToMiraiCode());
        }
    }

    @Override
    @EventHandler
    public void onPrivateMessageEvent(FriendMessageEvent event) {
        final Friend friend = event.getFriend();

        // 查找该用户的接待员
        final long qq = friend.getId();
        Receptionist receptionist = getOrPutReceptionist(qq);

        receptionist.getOrPutPrivateTask(friend).onMessage(event.getMessage().serializeToMiraiCode());
    }

    @Override
    @EventHandler
    public void onTempMessageEvent(GroupTempMessageEvent event) {
        final Group group = event.getGroup();
        final NormalMember member = event.getSender();

        final ResponseGroup responseGroup = getXiaomingBot().getResponseGroupManager().forCode(group.getId());

        // 不是响应群就算了
        if (Objects.isNull(responseGroup)) {
            return;
        }

        // 查找该用户的接待员
        final long qq = member.getId();
        Receptionist receptionist = getOrPutReceptionist(qq);

        receptionist.getOrPutTempTask(responseGroup, member).onMessage(event.getMessage().serializeToMiraiCode());
    }
}
