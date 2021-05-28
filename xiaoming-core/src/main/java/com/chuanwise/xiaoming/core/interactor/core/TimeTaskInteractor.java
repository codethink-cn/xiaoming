package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.time.task.TimeTask;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.StringUtil;
import com.chuanwise.xiaoming.api.util.TimeUtil;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;
import com.chuanwise.xiaoming.core.time.task.MessageTimeTaskImpl;
import net.mamoe.mirai.contact.Group;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class TimeTaskInteractor extends CommandInteractorImpl {
    static final String TIME = "(时间|time)";
    static final String TASK = "(任务|task)";

    @Filter(TIME + TASK)
    @RequirePermission("time.list")
    public void onListTimeTasks(XiaomingUser user) {
        final List<TimeTask> tasks = getXiaomingBot().getTimeTaskManager().getTasks();
        user.sendMessage("任务队列：" + StringUtil.getCollectionSummary(tasks, task -> {
            final boolean timeout = task.getTime() >= System.currentTimeMillis();
            final boolean periodic = task.isPeriodic();
            return task.getDescription() + "：" + (timeout ? "已过期" : TimeUtil.toTimeString(System.currentTimeMillis() - task.getTime()) + "后执行");
        }, "", "（无）", "\n"));
    }

    /*
    @Filter(CommandWords.ADD + CommandWords.MESSAGE + TASK)
    @RequirePermission("time.add")
    public void onAddMessageTask(XiaomingUser user) {
        user.sendMessage("这个任务将在什么时候执行呢？按照「yyyy-mm-rr hh:mm:ss」的格式告诉我吧");
        long executeTime = -1;
        while (executeTime != -1) {
            final String text = user.nextInput();
            executeTime = TimeUtil.parseMillis(text);
            if (executeTime == -1) {
                user.sendError("{}并不是一个合理的日期，重新说一下吧");
            }
        }

        long group = -1;
        if (user.inGroup()) {
            user.sendMessage("到时候要发送的是群消息还是私聊消息？告诉我「本群」、「私聊」或一个群号吧");
            while (group != -1) {
                final String text = user.nextInput();
                if (text.matches("\\d+")) {
                    group = Long.parseLong(text);
                } else if (Objects.equals(text, "本群")) {
                    group = user.getGroup().getId();
                } else if (Objects.equals(text, "私聊")) {
                    group = 0;
                }
                try {
                    if (group != 0) {
                        final Group miraiGroup = getXiaomingBot().getMiraiBot().getGroup(group);
                    }
                } catch (Exception exception) {
                    user.sendError("小明还不在这个群里，可能到时候会出现异常哦");
                }
                if (group == -1) {
                    user.sendError("{}并不是一个合理的日期，重新说一下吧");
                }
            }
        } else {
            user.sendMessage("到时候要发送的是群消息还是私聊消息？告诉我「私聊」或一个群号吧");
            while (group != -1) {
                final String text = user.nextInput();
                if (text.matches("\\d+")) {
                    group = Long.parseLong(text);
                } else if (Objects.equals(text, "私聊")) {
                    group = 0;
                }
                try {
                    if (group != 0) {
                        final Group miraiGroup = getXiaomingBot().getMiraiBot().getGroup(group);
                    }
                } catch (Exception exception) {
                    user.sendError("小明还不在这个群里，可能到时候会出现异常哦");
                }
                if (group == -1) {
                    user.sendError("{}并不是一个合理的日期，重新说一下吧");
                }
            }
        }

        user.sendMessage("到时候要给小明发送什么消息呢？");
        new MessageTimeTaskImpl(user.getQQ(), group, )
    }
     */
}
