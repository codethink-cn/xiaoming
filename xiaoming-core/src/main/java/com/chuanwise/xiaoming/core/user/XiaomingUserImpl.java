package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import io.ktor.util.collections.ConcurrentList;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.At;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 小明的使用者对象
 * @author Chuanwise
 */
@Getter
public abstract class XiaomingUserImpl extends HostObjectImpl implements XiaomingUser {
    @Setter
    Receptionist receptionist;

    @Setter
    Interactor interactor;

    public XiaomingUserImpl(XiaomingBot xiaomingBot, long qq) {
        super(xiaomingBot);
        setProperty("qq", qq);
        setProperty("at", new At(qq).serializeToMiraiCode());
    }

    /**
     * 发送消息的缓存机制
     */
    Stack<StringWriter> stringWriters = new Stack<>();
    Stack<PrintWriter> printWriters = new Stack<>();

    @Override
    public boolean isUsingBuffer() {
        return !stringWriters.empty();
    }

    @Override
    public void enableBuffer() {
        final StringWriter stringWriter = new StringWriter();
        stringWriters.add(stringWriter);
        printWriters.add(new PrintWriter(stringWriter));
    }

    @Override
    public void disableBuffer() {
        if (isUsingBuffer()) {
            stringWriters.pop();
            printWriters.pop();
        }
    }

    Map<String, Object> properties = new ConcurrentHashMap<>();

    Map<String, Set<Thread>> propertyWaiters = new ConcurrentHashMap<>();

    Set<Thread> globalMessageWaiter = new CopyOnWriteArraySet<>();

    @Setter
    List<String> globalNextMessage;
}