package com.chuanwise.xiaoming.api.contact.contact;

import com.chuanwise.xiaoming.api.contact.message.ConsoleMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;

import java.util.List;

public interface ConsoleContact extends XiaomingContact {
    @Override
    default Friend getMiraiContact() {
        return getXiaomingBot().getMiraiBot().getAsFriend();
    }

    @Override
    default String getName() {
        return "后台";
    }

    @Override
    default String getAvatarUrl() {
        return getMiraiContact().getAvatarUrl();
    }

    @Override
    default String getAlias() {
        return "后台";
    }

    @Override
    default String getCompleteName() {
        return "后台";
    }

    @Override
    List<ConsoleMessage> getRecentMessages();

    default void addRecentMessage(ConsoleMessage message) {
        final List<ConsoleMessage> list = getRecentMessages();
        list.add(message);
        synchronized (list) {
            list.notifyAll();
        }
    }
}
