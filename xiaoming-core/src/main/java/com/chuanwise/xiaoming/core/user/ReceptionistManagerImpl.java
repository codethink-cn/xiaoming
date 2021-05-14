package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.annotation.HandlerMethod;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.user.Receiptionist;
import com.chuanwise.xiaoming.api.user.ReceiptionistManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.event.EventListenerImpl;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
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
public class ReceptionistManagerImpl extends EventListenerImpl implements ReceiptionistManager {
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
    Map<Long, Receiptionist> receiptionists = new ConcurrentHashMap<>();

    @Override
    @HandlerMethod
    public void onGroupMessageEvent(GroupMessageEvent event) {
        final Group group = event.getGroup();
        final Member member = event.getSender();

        // 查找该用户的接待员
        final long qq = member.getId();
        Receiptionist receiptionist = getReceiptionist(qq);

        // 如果没找到，就马上构造
        if (Objects.isNull(receiptionist)) {
            receiptionist = new ReceptionistImpl(new XiaomingUserImpl(getXiaomingBot()));
            // 启动接待员线程
            getXiaomingBot().execute(receiptionist);
            registerReceiptionist(qq, receiptionist);
        }

        final XiaomingUser user = receiptionist.getUser();
        user.setAsGroupMember(member);
        user.setMessage(event.getMessage().serializeToMiraiCode());

        // 唤醒接待员
        synchronized (user) {
            user.notifyAll();
        }
    }

    @Override
    @HandlerMethod
    public void onPrivateMessageEvent(FriendMessageEvent event) {
        final Friend friend = event.getFriend();

        // 查找该用户的接待员
        final long qq = friend.getId();
        Receiptionist receiptionist = getReceiptionist(qq);

        // 如果没找到，就马上构造
        if (Objects.isNull(receiptionist)) {
            receiptionist = new ReceptionistImpl(new XiaomingUserImpl(getXiaomingBot()));
            // 启动接待员线程
            getXiaomingBot().execute(receiptionist);
            registerReceiptionist(qq, receiptionist);
        }

        final XiaomingUser user = receiptionist.getUser();
        user.setAsPrivate(friend);
        user.setMessage(event.getMessage().serializeToMiraiCode());

        // 唤醒接待员
        synchronized (user) {
            user.notifyAll();
        }
    }

    @Override
    @HandlerMethod
    public void onTempMessageEvent(GroupTempMessageEvent event) {
        final Group group = event.getGroup();
        final NormalMember sender = event.getSender();

        // 查找该用户的接待员
        final long qq = sender.getId();
        Receiptionist receiptionist = getReceiptionist(qq);

        // 如果没找到，就马上构造
        if (Objects.isNull(receiptionist)) {
            receiptionist = new ReceptionistImpl(new XiaomingUserImpl(getXiaomingBot()));
            // 启动接待员线程
            getXiaomingBot().execute(receiptionist);
            registerReceiptionist(qq, receiptionist);
        }

        final XiaomingUser user = receiptionist.getUser();
        user.setAsTempMember(sender);
        user.setMessage(event.getMessage().serializeToMiraiCode());

        // 唤醒接待员
        synchronized (user) {
            user.notifyAll();
        }
    }
}
