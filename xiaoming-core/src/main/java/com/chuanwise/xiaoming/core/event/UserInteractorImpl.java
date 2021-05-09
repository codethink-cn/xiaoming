package com.chuanwise.xiaoming.core.event;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.event.UserInteractRunnable;
import com.chuanwise.xiaoming.api.event.UserInteractor;
import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.interactor.MessageWaiter;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import com.chuanwise.xiaoming.api.user.TempXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.object.XiaomingObjectImpl;
import com.chuanwise.xiaoming.core.user.GroupXiaomingUserImpl;
import com.chuanwise.xiaoming.core.user.PrivateXiaomingUserImpl;
import com.chuanwise.xiaoming.core.user.TempXiaomingUserImpl;
import lombok.Data;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Member;

import java.util.Objects;

/**
 * 同一个 QQ 用户可能有三种形态。为避免反复创建，第一次就创建好。
 */
@Data
public class UserInteractorImpl extends XiaomingObjectImpl implements UserInteractor {
    final UserInteractRunnable userInteractRunnable;

    public UserInteractorImpl(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        userInteractRunnable = new UserInteractRunnableImpl(getXiaomingBot());
    }

    GroupXiaomingUser groupXiaomingUser;

    PrivateXiaomingUser privateXiaomingUser;

    TempXiaomingUser tempXiaomingUser;

    @Override
    public void setUser(XiaomingUser user) {
        if (user instanceof GroupXiaomingUser) {
            groupXiaomingUser = ((GroupXiaomingUser) user);
        } else if (user instanceof PrivateXiaomingUser) {
            privateXiaomingUser = ((PrivateXiaomingUser) user);
        } else {
            tempXiaomingUser = ((TempXiaomingUser) user);
        }
        userInteractRunnable.setUser(user);
        user.setUserInteractRunnable(userInteractRunnable);
        user.setUserInteractor(this);
    }

    @Override
    public void onGroupMessage(Member member, String message) {
        final XiaomingUser user = userInteractRunnable.getUser();

        // 如果之前没有用户，则只需要创建新的用户
        if (Objects.isNull(user)) {
            groupXiaomingUser = new GroupXiaomingUserImpl(getXiaomingBot(), member);
        } else {
            // 否则需要讨论用户穿越的问题
            if (user instanceof GroupXiaomingUser) {
                // 都是在群内的会话，但是穿越在不同的群之间
                if (((GroupXiaomingUser) user).getGroupNumber() != member.getGroup().getId()) {
                    ((GroupXiaomingUser) user).setAsGroupMember(member);
                }
            } else {
                // 群聊私聊之间的穿越，属于比较大的变化

                // 先寻找之前是否有群聊小明会话的实例，有就创建否则不必再次 new
                if (Objects.nonNull(groupXiaomingUser)) {
                    groupXiaomingUser.setAsGroupMember(member);
                } else {
                    groupXiaomingUser = new GroupXiaomingUserImpl(getXiaomingBot(), member);
                }

                // 如果当前正在上下文交互，就终止本次交互
                final MessageWaiter messageWaiter = userInteractRunnable.getMessageWaiter();
                if (Objects.nonNull(messageWaiter)) {
                    synchronized (messageWaiter) {
                        messageWaiter.notifyAll();
                    }
                }
                userInteractRunnable.setMessageWaiter(null);
                userInteractRunnable.setInteractor(null);
            }
        }

        // 设置新的输入
        groupXiaomingUser.setMessage(message);
        groupXiaomingUser.setUserInteractRunnable(userInteractRunnable);
        userInteractRunnable.setUser(groupXiaomingUser);

        // 消息等待器非空的话，上一次线程肯定是在这里等待。将之唤醒以进行本次交互
        final MessageWaiter messageWaiter = userInteractRunnable.getMessageWaiter();
        if (Objects.nonNull(messageWaiter)) {
            messageWaiter.onInput(message);
        } else {
            synchronized (this) {
                notifyAll();
            }
        }
    }

    @Override
    public void onTempMessage(Member member, String message) {
        final XiaomingUser user = userInteractRunnable.getUser();

        // 如果之前没有用户，则只需要创建新的用户
        if (Objects.isNull(user)) {
            tempXiaomingUser = new TempXiaomingUserImpl(getXiaomingBot(), member);
        } else {
            // 否则需要讨论用户穿越的问题
            if (!(user instanceof TempXiaomingUser)) {
                // 群聊私聊之间的穿越，属于比较大的变化

                // 先寻找之前是否有群聊小明会话的实例，有就创建否则不必再次 new
                if (Objects.nonNull(tempXiaomingUser)) {
                    tempXiaomingUser.setAsGroupMember(member);
                } else {
                    tempXiaomingUser = new TempXiaomingUserImpl(getXiaomingBot(), member);
                }

                // 如果当前正在上下文交互，就终止本次交互
                final MessageWaiter messageWaiter = userInteractRunnable.getMessageWaiter();
                if (Objects.nonNull(messageWaiter)) {
                    synchronized (messageWaiter) {
                        messageWaiter.notifyAll();
                    }
                }
                userInteractRunnable.setMessageWaiter(null);
                userInteractRunnable.setInteractor(null);
            }
        }

        // 设置新的输入
        tempXiaomingUser.setMessage(message);
        tempXiaomingUser.setUserInteractRunnable(userInteractRunnable);
        userInteractRunnable.setUser(tempXiaomingUser);

        // 消息等待器非空的话，上一次线程肯定是在这里等待。将之唤醒以进行本次交互
        final MessageWaiter messageWaiter = userInteractRunnable.getMessageWaiter();
        if (Objects.nonNull(messageWaiter)) {
            messageWaiter.onInput(message);
        } else {
            synchronized (this) {
                notifyAll();
            }
        }
    }

    @Override
    public void onPrivateMessage(Friend friend, String message) {
        final XiaomingUser user = userInteractRunnable.getUser();

        // 如果之前没有用户，则只需要创建新的用户
        if (Objects.isNull(user)) {
            privateXiaomingUser = new PrivateXiaomingUserImpl(getXiaomingBot(), friend);
        } else {
            // 否则需要讨论用户穿越的问题
            if (!(user instanceof PrivateXiaomingUser)) {
                // 群聊私聊之间的穿越，属于比较大的变化

                // 先寻找之前是否有私聊小明会话的实例，有就创建否则不必再次 new
                if (Objects.nonNull(privateXiaomingUser)) {
                    privateXiaomingUser.setFriend(friend);
                } else {
                    privateXiaomingUser = new PrivateXiaomingUserImpl(getXiaomingBot(), friend);
                }

                // 如果当前正在上下文交互，就终止本次交互
                final MessageWaiter messageWaiter = userInteractRunnable.getMessageWaiter();
                if (Objects.nonNull(messageWaiter)) {
                    synchronized (messageWaiter) {
                        messageWaiter.notifyAll();
                    }
                }
                userInteractRunnable.setMessageWaiter(null);
                userInteractRunnable.setInteractor(null);
            }
        }

        // 设置新的输入
        privateXiaomingUser.setMessage(message);
        privateXiaomingUser.setUserInteractRunnable(userInteractRunnable);
        userInteractRunnable.setUser(privateXiaomingUser);

        // 消息等待器非空的话，上一次线程肯定是在这里等待。将之唤醒以进行本次交互
        final MessageWaiter messageWaiter = userInteractRunnable.getMessageWaiter();
        if (Objects.nonNull(messageWaiter)) {
            messageWaiter.onInput(message);
        } else {
            synchronized (this) {
                notifyAll();
            }
        }
    }
}