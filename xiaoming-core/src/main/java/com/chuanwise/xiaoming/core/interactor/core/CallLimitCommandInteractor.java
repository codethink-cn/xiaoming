package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.limit.CallLimitConfig;
import com.chuanwise.xiaoming.api.limit.UserCallLimitManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.TimeUtil;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;

/**
 * 和小明调用限制相关的指令处理器
 * @author Chuanwise
 */
public class CallLimitCommandInteractor extends CommandInteractorImpl {
    static final String CALL_REGEX = "(调用|召唤|call)";

    final UserCallLimitManager callLimitManager;
    final CallLimitConfig config;

    public CallLimitCommandInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        callLimitManager = getXiaomingBot().getUserCallLimitManager();
        config = getXiaomingBot().getConfiguration().getGroupCallConfig();
    }

    @Filter(CommandWords.GROUP_REGEX + CALL_REGEX + CommandWords.LIMIT_REGEX)
    public void onLookGroupCallLimit(XiaomingUser user) {
        StringBuilder builder = new StringBuilder()
                .append("群内").append(TimeUtil.toTimeString(config.getPeriod())).append("内可以召唤")
                .append(config.getTop()).append("次小明，召唤技能冷却时间为")
                .append(TimeUtil.toTimeString(config.getCoolDown())).append("。").append("\n");

        if (user.hasPermission("limit.group.bypass")) {
            builder.append("但是你不受召唤限制哦");
            user.sendMessage(builder.toString());
            return;
        }

        /*
        final UserCallRecord callRecords = callLimitManager.getGroupCallLimiter().getCallRecords(user.getQQ());
        if (Objects.isNull(callRecords)) {
            builder.append("你还可以在群里召唤").append(config.getTop() - 1).append("次小明哦");
        } else {
            final Long[] list = callRecords.list();
            int index = 0;
            for (; index < list.length; index++) {
                if (list[index] + config.getPeriod() > System.currentTimeMillis()) {
                    break;
                }
            }
            final int remainCallTimes = config.getTop() - index - 1;
            if (remainCallTimes == 0) {
                builder.append("本次召唤正好是最近的最后一次召唤机会");
            } else {
                builder.append("你还可以在群里召唤").append(remainCallTimes).append("次小明哦");
            }
        }
         */
        user.sendMessage(builder.toString());
    }

    @Filter(CommandWords.GROUP_REGEX + CALL_REGEX + CommandWords.LIMIT_REGEX + " (周期|period) {time}")
    @RequirePermission("limit.group.period")
    public void onSetGroupPeriod(XiaomingUser user,
                                 @FilterParameter("time") final String timeString) {
        final long time = TimeUtil.parseTime(timeString);
        if (time == -1) {
            user.sendMessage("{}并不是一个合理的时间哦", timeString);
        } else {
            final CallLimitConfig groupCallLimitConfig = callLimitManager.getGroupCallLimiter().getConfig();
            groupCallLimitConfig.setPeriod(time);
            getXiaomingBot().getRegularPreserveManager().readySave(getXiaomingBot().getConfiguration());
            user.sendMessage("成功设置群内召唤周期为{}，在这段时间内最多召唤{}次小明",
                    TimeUtil.toTimeString(time),
                    groupCallLimitConfig.getTop());
        }
    }

    @Filter(CommandWords.GROUP_REGEX + CALL_REGEX + CommandWords.LIMIT_REGEX + " (上限|top) {time}")
    @RequirePermission("limit.group.top")
    public void onSetGroupTop(XiaomingUser user,
                              @FilterParameter("time") final String timeString) {
        if (!timeString.matches("\\d+")) {
            user.sendError("{}并不是一个合理的数字哦", timeString);
        }
        final CallLimitConfig groupCallLimitConfig = callLimitManager.getGroupCallLimiter().getConfig();
        int time = Integer.parseInt(timeString);
        groupCallLimitConfig.setTop(time);
        getXiaomingBot().getRegularPreserveManager().readySave(getXiaomingBot().getConfiguration());
        callLimitManager.getGroupCallLimiter().getRecords().clear();
        user.sendMessage("成功设置群内召唤上限为{}次，已清空所有纪录。未来每{}内最多召唤这么多次小明",
                time,
                TimeUtil.toTimeString(groupCallLimitConfig.getPeriod()));
    }

    @Filter(CommandWords.GROUP_REGEX + CALL_REGEX + CommandWords.LIMIT_REGEX + " (冷却时间|冷却|cooldown) {time}")
    @RequirePermission("limit.group.cooldown")
    public void onSetGroupCoolDown(XiaomingUser user,
                                   @FilterParameter("time") final String timeString) {
        if (!timeString.matches("\\d+")) {
            user.sendError("{}并不是一个合理的数字哦", timeString);
        }
        int time = Integer.parseInt(timeString);
        if (time > TimeUtil.SECOND_MINS * 30) {
            user.sendError("过长的召唤冷却时间会影响使用体验");
        }
        final CallLimitConfig config = callLimitManager.getGroupCallLimiter().getConfig();
        config.setCoolDown(time);
        getXiaomingBot().getRegularPreserveManager().readySave(getXiaomingBot().getConfiguration());
        user.sendMessage("成功设置小明的召唤冷却时间为{}", TimeUtil.toTimeString(config.getPeriod()));
    }

    @Filter(CommandWords.PRIVATE_REGEX + CALL_REGEX + CommandWords.LIMIT_REGEX)
    public void onLookPrivateCallLimit(XiaomingUser user) {
        StringBuilder builder = new StringBuilder()
                .append("私聊").append(TimeUtil.toTimeString(config.getPeriod())).append("内可以召唤")
                .append(config.getTop()).append("次小明，召唤技能冷却时间为")
                .append(TimeUtil.toTimeString(config.getCoolDown())).append("。").append("\n");


        if (user.hasPermission("limit.private.bypass")) {
            builder.append("但是你不受召唤限制哦");
            user.sendMessage(builder.toString());
            return;
        }
        /*
        final UserCallRecord callRecords = callLimitManager.getPrivateCallLimiter().getCallRecords(user.getQQ());
        if (Objects.isNull(callRecords)) {
            builder.append("你还可以私聊召唤").append(config.getTop() - 1).append("次小明哦");
        } else {
            final Long[] list = callRecords.list();
            int index = 0;
            for (; index < list.length; index++) {
                if (list[index] + config.getPeriod() > System.currentTimeMillis()) {
                    break;
                }
            }
            final int remainCallTimes = config.getTop() - index - 1;
            if (remainCallTimes == 0) {
                builder.append("本次召唤正好是最近的最后一次召唤机会");
            } else {
                builder.append("你还可以私聊召唤").append(remainCallTimes).append("次小明哦");
            }
        }
         */
        user.sendMessage(builder.toString());
    }

    @Filter(CommandWords.PRIVATE_REGEX + CALL_REGEX + CommandWords.LIMIT_REGEX + " (周期|period) {time}")
    @RequirePermission("limit.private.period")
    public void onSetPrivatePeriod(XiaomingUser user,
                                 @FilterParameter("time") final String timeString) {
        final long time = TimeUtil.parseTime(timeString);
        if (time == -1) {
            user.sendMessage("{}并不是一个合理的时间哦", timeString);
        } else {
            final CallLimitConfig config = callLimitManager.getPrivateCallLimiter().getConfig();
            config.setPeriod(time);
            getXiaomingBot().getRegularPreserveManager().readySave(getXiaomingBot().getConfiguration());
            user.sendMessage("成功设置私聊召唤周期为{}，在这段时间内最多召唤{}次小明",
                    TimeUtil.toTimeString(time),
                    config.getTop());
        }
    }

    @Filter(CommandWords.PRIVATE_REGEX + CALL_REGEX + CommandWords.LIMIT_REGEX + " (上限|top) {time}")
    @RequirePermission("limit.private.top")
    public void onSetPrivateTop(XiaomingUser user,
                              @FilterParameter("time") final String timeString) {
        if (!timeString.matches("\\d+")) {
            user.sendError("{}并不是一个合理的数字哦", timeString);
        }
        final CallLimitConfig config = callLimitManager.getPrivateCallLimiter().getConfig();
        int time = Integer.parseInt(timeString);
        config.setTop(time);
        getXiaomingBot().getRegularPreserveManager().readySave(getXiaomingBot().getConfiguration());
        callLimitManager.getPrivateCallLimiter().getRecords().clear();
        user.sendMessage("成功设置私聊召唤上限为{}次，已清空所有纪录。未来每{}内最多召唤这么多次小明",
                time,
                TimeUtil.toTimeString(config.getPeriod()));
    }

    @Filter(CommandWords.PRIVATE_REGEX + CALL_REGEX + CommandWords.LIMIT_REGEX + " (冷却时间|冷却|cooldown) {time}")
    @RequirePermission("limit.private.cooldown")
    public void onSetPrivateCoolDown(XiaomingUser user,
                                   @FilterParameter("time") final String timeString) {
        if (!timeString.matches("\\d+")) {
            user.sendError("{}并不是一个合理的数字哦", timeString);
        }
        int time = Integer.parseInt(timeString);
        if (time > TimeUtil.SECOND_MINS * 30) {
            user.sendError("过长的召唤冷却时间会影响使用体验");
        }
        final CallLimitConfig config = callLimitManager.getPrivateCallLimiter().getConfig();
        config.setCoolDown(time);
        getXiaomingBot().getRegularPreserveManager().readySave(getXiaomingBot().getConfiguration());
        user.sendMessage("成功设置小明的私聊召唤冷却时间为{}", TimeUtil.toTimeString(config.getPeriod()));
    }
}