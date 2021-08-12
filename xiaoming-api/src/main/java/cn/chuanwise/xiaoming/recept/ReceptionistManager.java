package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.annotation.EventHandler;
import cn.chuanwise.xiaoming.event.EventListener;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.optimize.Optimizable;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import org.slf4j.Logger;

import java.beans.Transient;
import java.util.Map;

public interface ReceptionistManager extends ModuleObject, EventListener, Optimizable {
    /**
     * 获得某用户的接待员
     * @param code 用户 QQ
     * @return 其接待员。如果无此接待员，返回 {@code null}
     */
    Receptionist forReceptionist(long code);

    Receptionist getBotReceptionist();

    /**
     * 取消某个用户的接待员
     * @param code 该用户
     */
    default void removeReceptionist(long code) {
        getReceptionists().remove(code);
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
    void onMemberMessageEvent(GroupTempMessageEvent event);

    /**
     * 获得接待员记录器
     * @return 接待员的 Map
     */
    Map<Long, Receptionist> getReceptionists();

    @Transient
    @Override
    Logger getLogger();

    @Override
    default void optimize() {
        getReceptionists().values().forEach(Receptionist::optimize);
    }

    default void close() {
        optimize();
        getReceptionists().values().forEach(Receptionist::stop);
    }
}