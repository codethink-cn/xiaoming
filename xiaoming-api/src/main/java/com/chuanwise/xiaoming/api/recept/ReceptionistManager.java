package com.chuanwise.xiaoming.api.recept;

import com.chuanwise.xiaoming.api.annotation.EventHandler;
import com.chuanwise.xiaoming.api.event.EventListener;
import com.chuanwise.xiaoming.api.object.HostObject;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Map;

public interface ReceptionistManager extends HostObject, EventListener {
    /**
     * 获得某用户的接待员
     * @param qq 用户 QQ
     * @return 其接待员。如果无此接待员，返回 {@code null}
     */
    @Nullable
    default Receptionist getReceptionist(long qq) {
        return getReceptionists().get(qq);
    }

    /**
     * 取消某个用户的接待员
     * @param qq 该用户
     */
    default void removeReceptionist(long qq) {
        getReceptionists().remove(qq);
    }

    /**
     * 标准的小明群聊交互事件响应器
     * @param event 来自 mirai 的群消息事件
     */
    @EventHandler
    void onGroupMessageEvent(GroupMessageEvent event);

    /**
     * 标准的小明私聊事件响应器
     * @param event 来自 mirai 的私聊事件
     */
    @EventHandler
    void onPrivateMessageEvent(FriendMessageEvent event);

    /**
     * 标准的小明临时会话事件响应器
     * @param event 来自 mirai 的临时会话消息事件
     */
    @EventHandler
    void onTempMessageEvent(GroupTempMessageEvent event);

    /**
     * 获得接待员记录器
     * @return 接待员的 Map
     */
    Map<Long, Receptionist> getReceptionists();

    @Override
    Logger getLog();

    default void optimize() {
        getReceptionists().values().forEach(Receptionist::optimize);
    }
}
