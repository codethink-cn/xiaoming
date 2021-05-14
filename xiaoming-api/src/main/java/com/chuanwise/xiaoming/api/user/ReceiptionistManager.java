package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.annotation.HandlerMethod;
import com.chuanwise.xiaoming.api.event.EventListener;
import com.chuanwise.xiaoming.api.object.HostObject;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public interface ReceiptionistManager extends HostObject, EventListener {
    /**
     * 获得某用户的接待员
     * @param qq 用户 QQ
     * @return 其接待员。如果无此接待员，返回 {@code null}
     */
    @Nullable
    default Receiptionist getReceiptionist(long qq) {
        return getReceiptionists().get(qq);
    }

    /**
     * 取消某个用户的接待员
     * @param qq 该用户
     */
    default void removeReceiptionist(long qq) {
        getReceiptionists().remove(qq);
    }

    /**
     * 设置某个用户的接待员。如果它之前有一个招待员，则将其关闭
     * @param qq 该用户
     * @param receiptionist 新的接待员
     */
    default void registerReceiptionist(long qq, Receiptionist receiptionist) {
        final Receiptionist elderReceiption = getReceiptionist(qq);
        if (Objects.nonNull(elderReceiption) && elderReceiption.isRunning()) {
            elderReceiption.stop();
        }
        getReceiptionists().put(qq, receiptionist);
    }

    /**
     * 标准的小明群聊交互事件响应器
     * @param event 来自 mirai 的群消息事件
     */
    @HandlerMethod
    void onGroupMessageEvent(GroupMessageEvent event);

    /**
     * 标准的小明私聊事件响应器
     * @param event 来自 mirai 的私聊事件
     */
    @HandlerMethod
    void onPrivateMessageEvent(FriendMessageEvent event);

    /**
     * 标准的小明临时会话事件响应器
     * @param event 来自 mirai 的临时会话消息事件
     */
    @HandlerMethod
    void onTempMessageEvent(GroupTempMessageEvent event);

    /**
     * 获得接待员记录器
     * @return 接待员的 Map
     */
    Map<Long, Receiptionist> getReceiptionists();
}
