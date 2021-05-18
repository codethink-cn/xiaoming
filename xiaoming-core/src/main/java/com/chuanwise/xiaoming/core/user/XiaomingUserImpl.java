package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import io.ktor.util.collections.ConcurrentList;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 小明的使用者对象
 * @author Chuanwise
 */
@Getter
public class XiaomingUserImpl extends HostObjectImpl implements XiaomingUser {
    @Setter
    Receptionist receptionist;

    public XiaomingUserImpl(XiaomingBot xiaomingBot, long qq) {
        super(xiaomingBot);
        this.qq = qq;
        getLog().warn("constructor user: {}", qq);
    }

    long qq;

    @Override
    public long getQQ() {
        return qq;
    }

    /**
     * 发送消息的缓存机制
     */
    StringBuilder buffer = new StringBuilder();

    @Setter
    boolean usingBuffer;

    Map<String, Object> properties = new ConcurrentHashMap<>();

    Map<String, Set<Thread>> propertyWaiters = new ConcurrentHashMap<>();

    Map<Long, List<String>> recentGroupMessages = new ConcurrentHashMap<>();

    Map<Long, List<String>> recentTempMessages = new ConcurrentHashMap<>();

    List<String> recentPrivateMessage = new ConcurrentList<>();
}