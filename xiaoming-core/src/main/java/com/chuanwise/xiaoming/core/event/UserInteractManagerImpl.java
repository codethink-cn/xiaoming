package com.chuanwise.xiaoming.core.event;

import com.chuanwise.xiaoming.api.annotation.HandlerMethod;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.event.UserInteractManager;
import com.chuanwise.xiaoming.api.event.UserInteractor;
import com.chuanwise.xiaoming.api.limit.CallLimitConfig;
import com.chuanwise.xiaoming.api.limit.CallRecord;
import com.chuanwise.xiaoming.api.limit.UserCallLimitManager;
import com.chuanwise.xiaoming.api.limit.UserCallLimiter;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import com.chuanwise.xiaoming.api.user.TempXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.TimeUtil;
import com.chuanwise.xiaoming.core.command.executor.GlobalCommandExecutor;
import com.chuanwise.xiaoming.core.user.GroupXiaomingUserImpl;
import com.chuanwise.xiaoming.core.user.PrivateXiaomingUserImpl;
import com.chuanwise.xiaoming.core.user.TempXiaomingUserImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Chuanwise
 */
@Getter
public class UserInteractManagerImpl extends EventListenerImpl implements UserInteractManager {

    /**
     * 全局指令处理器
     */
    GlobalCommandExecutor globalCommandExecutor = new GlobalCommandExecutor();

    /**
     * 日志记录器
     */
    Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 和每个用户交互时的线程隔离器
     */
    Map<Long, UserInteractor> islocator = new ConcurrentHashMap<>();

    public UserInteractManagerImpl(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        getXiaomingBot().getCommandManager().register(globalCommandExecutor, null);
    }

    @Override
    public UserInteractor getIslocator(long qq) {
        return islocator.get(qq);
    }

    /**
     * 群消息响应方法
     * @param event
     */
    @HandlerMethod
    public void onGroupMessage(GroupMessageEvent event) {
        final Group group = event.getGroup();
        final Member member = event.getSender();

        // 寻找与该用户交互的线程
        final long qq = member.getId();
        UserInteractor userInteractor = islocator.get(qq);

        // 第一次启动线程
        boolean runnableUnstart = false;
        if (Objects.isNull(userInteractor)) {
            userInteractor = new UserInteractorImpl(getXiaomingBot());
            islocator.put(qq, userInteractor);
            runnableUnstart = true;
        }

        XiaomingUser user = userInteractor.getGroupXiaomingUser();
        if (Objects.isNull(user)) {
            final GroupXiaomingUserImpl groupXiaomingUser = new GroupXiaomingUserImpl(getXiaomingBot(), member);
            user = groupXiaomingUser;
            userInteractor.setUser(user);
        }

        if (runnableUnstart) {
            getXiaomingBot().execute(user.getUserInteractRunnable());
        }

        // 检查是否达到调用限制
        final UserCallLimitManager userCallLimitManager = getXiaomingBot().getUserCallLimitManager();
        final UserCallLimiter groupCallLimiter = userCallLimitManager.getGroupCallLimiter();
        // 如果没权限跳过，且有调用记录，并且很长一段时间没有提醒调用太多次
        if (!getXiaomingBot().getPermissionManager().userHasPermission(qq, "limit.group.bypass")) {
            if (groupCallLimiter.isTooManySoUncallable(qq) && groupCallLimiter.shouldNotice(qq)) {
                final CallLimitConfig config = groupCallLimiter.getConfig();
                final CallRecord userCallRecords = groupCallLimiter.getOrPutCallRecords(qq);

                ((GroupXiaomingUser) user).sendPrivateMessage("你" + TimeUtil.toTimeString(config.getPeriod()) + "内已经在群里召唤了" + config.getTop() + "次小明，" +
                        "好好休息一下吧 " + getXiaomingBot().getWordManager().get("happy") + "，" +
                        TimeUtil.after(userCallRecords.getEarlyestRecord(), config.getDeltaNoticeTime()) + "就可以继续在群里召唤我啦");

                groupCallLimiter.setNoticed(qq);
                getXiaomingBot().getRegularPreserveManager().readySave(userCallLimitManager);
            }
            if (groupCallLimiter.uncallable(qq)) {
                return;
            }
        }

        // 如果本群没有启动小明，那就只是使用全局交互器
        final ResponseGroup responseGroup = getXiaomingBot().getResponseGroupManager().fromCode(group.getId());
        if (Objects.isNull(responseGroup) || !responseGroup.hasTag(GlobalCommandExecutor.ENABLE_TAG)) {
            user.setMessage(event.getMessage().serializeToMiraiCode());
            try {
                globalCommandExecutor.onCommand(user);
            } catch (Exception exception) {
                exception.printStackTrace();
                user.sendError("小明遇到了一些问题：{}", exception);
            }
        } else {
            userInteractor.onGroupMessage(member, event.getMessage().serializeToMiraiCode());
        }
    }

    /**
     * 群临时消息响应方法
     * @param event
     */
    @HandlerMethod
    public void onGroupTempMessage(GroupTempMessageEvent event) {
        final Group group = event.getGroup();
        final Member member = event.getSender();

        // 寻找与该用户交互的线程
        final long qq = member.getId();
        UserInteractor userInteractor = islocator.get(qq);

        boolean runnableUnstart = false;
        if (Objects.isNull(userInteractor)) {
            userInteractor = new UserInteractorImpl(getXiaomingBot());
            islocator.put(qq, userInteractor);
            runnableUnstart = true;
        }

        XiaomingUser user = userInteractor.getTempXiaomingUser();
        if (Objects.isNull(user)) {
            final TempXiaomingUser tempXiaomingUser = new TempXiaomingUserImpl(getXiaomingBot(), member);
            user = tempXiaomingUser;
            userInteractor.setUser(user);
        }

        if (runnableUnstart) {
            getXiaomingBot().execute(user.getUserInteractRunnable());
        }

        // 检查是否达到调用限制
        final UserCallLimitManager userCallLimitManager = getXiaomingBot().getUserCallLimitManager();
        final UserCallLimiter groupCallLimiter = userCallLimitManager.getPrivateCallLimiter();
        // 如果没权限跳过，且有调用记录，并且很长一段时间没有提醒调用太多次
        if (!getXiaomingBot().getPermissionManager().userHasPermission(qq, "limit.private.bypass")) {
            if (groupCallLimiter.isTooManySoUncallable(qq) && groupCallLimiter.shouldNotice(qq)) {
                final CallLimitConfig config = groupCallLimiter.getConfig();
                final CallRecord userCallRecords = groupCallLimiter.getOrPutCallRecords(qq);

                ((GroupXiaomingUser) user).sendPrivateMessage("你" + TimeUtil.toTimeString(config.getPeriod()) + "内已经私聊召唤了" + config.getTop() + "次小明，" +
                        "好好休息一下吧 " + getXiaomingBot().getWordManager().get("happy") + "，" +
                        TimeUtil.after(userCallRecords.getEarlyestRecord(), config.getDeltaNoticeTime()) + "就可以继续私聊召唤我啦");

                groupCallLimiter.setNoticed(qq);
                getXiaomingBot().getRegularPreserveManager().readySave(userCallLimitManager);
            }
            if (groupCallLimiter.uncallable(qq)) {
                return;
            }
        }

        userInteractor.onTempMessage(member, event.getMessage().serializeToMiraiCode());
    }

    /**
     * 私聊消息响应方法
     * @param event
     */
    @HandlerMethod
    public void onPrivateMessage(FriendMessageEvent event) {
        final Friend friend = event.getFriend();

        // 寻找与该用户交互的线程
        final long qq = friend.getId();
        UserInteractor userInteractor = islocator.get(qq);

        boolean runnableUnstart = false;
        if (Objects.isNull(userInteractor)) {
            userInteractor = new UserInteractorImpl(getXiaomingBot());
            islocator.put(qq, userInteractor);
            runnableUnstart = true;
        }

        XiaomingUser user = userInteractor.getPrivateXiaomingUser();
        if (Objects.isNull(user)) {
            final PrivateXiaomingUser privateXiaomingUser = new PrivateXiaomingUserImpl(getXiaomingBot(), friend);
            user = privateXiaomingUser;
            userInteractor.setUser(user);
        }

        if (runnableUnstart) {
            getXiaomingBot().execute(user.getUserInteractRunnable());
        }

        // 检查是否达到调用限制
        final UserCallLimitManager userCallLimitManager = getXiaomingBot().getUserCallLimitManager();
        final UserCallLimiter groupCallLimiter = userCallLimitManager.getPrivateCallLimiter();
        // 如果没权限跳过，且有调用记录，并且很长一段时间没有提醒调用太多次
        if (!getXiaomingBot().getPermissionManager().userHasPermission(qq, "limit.private.bypass")) {
            if (groupCallLimiter.isTooManySoUncallable(qq) && groupCallLimiter.shouldNotice(qq)) {
                final CallLimitConfig config = groupCallLimiter.getConfig();
                final CallRecord userCallRecords = groupCallLimiter.getOrPutCallRecords(qq);

                ((GroupXiaomingUser) user).sendPrivateMessage("你" + TimeUtil.toTimeString(config.getPeriod()) + "内已经私聊召唤了" + config.getTop() + "次小明，" +
                        "好好休息一下吧 " + getXiaomingBot().getWordManager().get("happy") + "，" +
                        TimeUtil.after(userCallRecords.getEarlyestRecord(), config.getDeltaNoticeTime()) + "就可以继续私聊召唤我啦");

                groupCallLimiter.setNoticed(qq);
                getXiaomingBot().getRegularPreserveManager().readySave(userCallLimitManager);
            }
            if (groupCallLimiter.uncallable(qq)) {
                return;
            }
        }

        userInteractor.onPrivateMessage(friend, event.getMessage().serializeToMiraiCode());
    }

    @Override
    public Logger getLog() {
        return log;
    }
}
