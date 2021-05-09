package com.chuanwise.xiaoming.api.event;

import com.chuanwise.xiaoming.api.event.UserInteractRunnable;
import com.chuanwise.xiaoming.api.object.HostXiaomingObject;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import net.mamoe.mirai.event.ListenerHost;

import java.util.Map;

/**
 * 用户交互线程管理器
 * @author Chuanwise
 */
public interface UserInteractManager extends XiaomingObject, HostXiaomingObject, EventListener {
    /**
     * 获得和一个用户交互时的线程
     * @param qq 该用户的 QQ
     * @return 如果该用户正在和小明交互，则返回对应的线程，否则返回 {@code null}
     */
    UserInteractor getIslocator(long qq);

    Map<Long, UserInteractor> getIslocator();
}
