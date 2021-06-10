package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.Require;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.limit.CallLimitConfig;
import com.chuanwise.xiaoming.api.limit.UserCallLimitManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.TimeUtils;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;

/**
 * 和小明调用限制相关的指令处理器
 * @author Chuanwise
 */
public class CallLimitCommandInteractor extends CommandInteractorImpl {
    final UserCallLimitManager callLimitManager;
    final CallLimitConfig groupCallConfig, privateCallConfig;

    public CallLimitCommandInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        callLimitManager = getXiaomingBot().getUserCallLimitManager();
        groupCallConfig = getXiaomingBot().getConfiguration().getGroupCallConfig();
        privateCallConfig = getXiaomingBot().getConfiguration().getPrivateCallConfig();
    }

    @Filter(CommandWords.GROUP + CommandWords.CALL + CommandWords.LIMIT)
    @Require("limit.group.look")
    public void onLookGroupCallLimit(XiaomingUser user) {
        StringBuilder builder = new StringBuilder()
                .append("群内").append(TimeUtils.toTimeString(groupCallConfig.getPeriod())).append("内可以召唤")
                .append(groupCallConfig.getTop()).append("次小明，召唤技能冷却时间为")
                .append(TimeUtils.toTimeString(groupCallConfig.getCoolDown())).append("。").append("\n");

        if (user.hasPermission("limit.group.bypass")) {
            builder.append("但是你不受召唤限制哦");
            user.sendMessage(builder.toString());
            return;
        }
        user.sendMessage(builder.toString());
    }

    @Filter(CommandWords.GROUP + CommandWords.CALL + CommandWords.LIMIT + " (周期|period) {time}")
    @Require("limit.group.period")
    public void onSetGroupPeriod(XiaomingUser user,
                                 @FilterParameter("time") final String timeString) {
        final long time = TimeUtils.parseTime(timeString);
        if (time == -1) {
            user.sendMessage("{}并不是一个合理的时间哦", timeString);
        } else {
            final CallLimitConfig groupCallLimitConfig = callLimitManager.getGroupCallLimiter().getConfig();
            groupCallLimitConfig.setPeriod(time);
            getXiaomingBot().getFinalizer().readySave(getXiaomingBot().getConfiguration());
            user.sendMessage("成功设置群内召唤周期为{}，在这段时间内最多召唤{}次小明",
                    TimeUtils.toTimeString(time),
                    groupCallLimitConfig.getTop());
        }
    }

    @Filter(CommandWords.GROUP + CommandWords.CALL + CommandWords.LIMIT + " (上限|top) {time}")
    @Require("limit.group.top")
    public void onSetGroupTop(XiaomingUser user,
                              @FilterParameter("time") final String timeString) {
        if (!timeString.matches("\\d+")) {
            user.sendError("{}并不是一个合理的数字哦", timeString);
        }
        final CallLimitConfig groupCallLimitConfig = callLimitManager.getGroupCallLimiter().getConfig();
        int time = Integer.parseInt(timeString);
        groupCallLimitConfig.setTop(time);
        getXiaomingBot().getFinalizer().readySave(getXiaomingBot().getConfiguration());
        callLimitManager.getGroupCallLimiter().getRecords().clear();
        user.sendMessage("成功设置群内召唤上限为{}次，已清空所有纪录。未来每{}内最多召唤这么多次小明",
                time,
                TimeUtils.toTimeString(groupCallLimitConfig.getPeriod()));
    }

    @Filter(CommandWords.GROUP + CommandWords.CALL + CommandWords.LIMIT + " (冷却时间|冷却|cooldown) {time}")
    @Require("limit.group.cooldown")
    public void onSetGroupCoolDown(XiaomingUser user,
                                   @FilterParameter("time") final String timeString) {
        if (!timeString.matches("\\d+")) {
            user.sendError("{}并不是一个合理的数字哦", timeString);
        }
        int time = Integer.parseInt(timeString);
        if (time > TimeUtils.SECOND_MINS * 30) {
            user.sendError("过长的召唤冷却时间会影响使用体验");
        }
        final CallLimitConfig config = callLimitManager.getGroupCallLimiter().getConfig();
        config.setCoolDown(time);
        getXiaomingBot().getFinalizer().readySave(getXiaomingBot().getConfiguration());
        user.sendMessage("成功设置小明的召唤冷却时间为{}", TimeUtils.toTimeString(config.getPeriod()));
    }

    @Filter(CommandWords.PRIVATE + CommandWords.CALL + CommandWords.LIMIT)
    @Require("limit.private.look")
    public void onLookPrivateCallLimit(XiaomingUser user) {
        StringBuilder builder = new StringBuilder()
                .append("私聊").append(TimeUtils.toTimeString(privateCallConfig.getPeriod())).append("内可以召唤")
                .append(privateCallConfig.getTop()).append("次小明，召唤技能冷却时间为")
                .append(TimeUtils.toTimeString(privateCallConfig.getCoolDown())).append("。").append("\n");


        if (user.hasPermission("limit.private.bypass")) {
            builder.append("但是你不受召唤限制哦");
            user.sendMessage(builder.toString());
            return;
        }
        user.sendMessage(builder.toString());
    }

    @Filter(CommandWords.PRIVATE + CommandWords.CALL + CommandWords.LIMIT + " (周期|period) {time}")
    @Require("limit.private.period")
    public void onSetPrivatePeriod(XiaomingUser user,
                                 @FilterParameter("time") final String timeString) {
        final long time = TimeUtils.parseTime(timeString);
        if (time == -1) {
            user.sendMessage("{}并不是一个合理的时间哦", timeString);
        } else {
            final CallLimitConfig config = callLimitManager.getPrivateCallLimiter().getConfig();
            config.setPeriod(time);
            getXiaomingBot().getFinalizer().readySave(getXiaomingBot().getConfiguration());
            user.sendMessage("成功设置私聊召唤周期为{}，在这段时间内最多召唤{}次小明",
                    TimeUtils.toTimeString(time),
                    config.getTop());
        }
    }

    @Filter(CommandWords.PRIVATE + CommandWords.CALL + CommandWords.LIMIT + " (上限|top) {time}")
    @Require("limit.private.top")
    public void onSetPrivateTop(XiaomingUser user,
                              @FilterParameter("time") final String timeString) {
        if (!timeString.matches("\\d+")) {
            user.sendError("{}并不是一个合理的数字哦", timeString);
        }
        final CallLimitConfig config = callLimitManager.getPrivateCallLimiter().getConfig();
        int time = Integer.parseInt(timeString);
        config.setTop(time);
        getXiaomingBot().getFinalizer().readySave(getXiaomingBot().getConfiguration());
        callLimitManager.getPrivateCallLimiter().getRecords().clear();
        user.sendMessage("成功设置私聊召唤上限为{}次，已清空所有纪录。未来每{}内最多召唤这么多次小明",
                time,
                TimeUtils.toTimeString(config.getPeriod()));
    }

    @Filter(CommandWords.PRIVATE + CommandWords.CALL + CommandWords.LIMIT + " (冷却时间|冷却|cooldown) {time}")
    @Require("limit.private.cooldown")
    public void onSetPrivateCoolDown(XiaomingUser user,
                                   @FilterParameter("time") final String timeString) {
        if (!timeString.matches("\\d+")) {
            user.sendError("{}并不是一个合理的数字哦", timeString);
        }
        int time = Integer.parseInt(timeString);
        if (time > TimeUtils.SECOND_MINS * 30) {
            user.sendError("过长的召唤冷却时间会影响使用体验");
        }
        final CallLimitConfig config = callLimitManager.getPrivateCallLimiter().getConfig();
        config.setCoolDown(time);
        getXiaomingBot().getFinalizer().readySave(getXiaomingBot().getConfiguration());
        user.sendMessage("成功设置小明的私聊召唤冷却时间为{}", TimeUtils.toTimeString(config.getPeriod()));
    }
}