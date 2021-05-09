package com.chuanwise.xiaoming.api.event;

import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.interactor.MessageWaiter;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Member;

import java.util.function.Function;

public interface UserInteractRunnable extends Runnable {
    /**
     * 获取该用户下一个输入
     * @param waitTime 最长等待时间
     * @param onTimeout 超时后的回调函数
     * @return 用户下一次输入的值，超时时返回 {@code null}
     */
    String getNextInput(long waitTime, Function<Void, Void> onTimeout);

    /**
     * 和用户交互
     * @throws Exception
     */
    boolean interact() throws Exception;

    XiaomingUser getUser();

    MessageWaiter getMessageWaiter();

    void setMessageWaiter(MessageWaiter messageWaiter);

    Interactor getInteractor();

    void setInteractor(Interactor interactor);

    void setUser(XiaomingUser user);
}
