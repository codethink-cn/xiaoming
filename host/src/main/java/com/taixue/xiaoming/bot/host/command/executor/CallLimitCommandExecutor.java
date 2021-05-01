package com.taixue.xiaoming.bot.host.command.executor;

import com.taixue.xiaoming.bot.api.annotation.RequirePermission;
import com.taixue.xiaoming.bot.api.annotation.Command;
import com.taixue.xiaoming.bot.api.annotation.CommandParameter;
import com.taixue.xiaoming.bot.api.limit.CallLimitConfig;
import com.taixue.xiaoming.bot.api.limit.UserCallLimitManager;
import com.taixue.xiaoming.bot.api.limit.UserCallRecord;
import com.taixue.xiaoming.bot.api.user.QQXiaomingUser;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.command.executor.CommandExecutorImpl;
import com.taixue.xiaoming.bot.util.CommandWordUtil;
import com.taixue.xiaoming.bot.util.TimeUtil;

import java.util.Objects;

/**
 * 和小明调用限制相关的指令处理器
 * @author Chuanwise
 */
public class CallLimitCommandExecutor extends CommandExecutorImpl {
    private static final String CALL_REGEX = "(调用|召唤|call)";
    private UserCallLimitManager callLimitManager = getXiaomingBot().getUserCallLimitManager();

    @Command(CommandWordUtil.GROUP_REGEX + CALL_REGEX + CommandWordUtil.LIMIT_REGEX)
    public void onLookGroupCallLimit(final QQXiaomingUser user) {
        final CallLimitConfig config = callLimitManager.getGroupCallLimiter().getConfig();
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

    @Command(CommandWordUtil.GROUP_REGEX + CALL_REGEX + CommandWordUtil.LIMIT_REGEX + " (周期|period) {time}")
    @RequirePermission("limit.group.period")
    public void onSetGroupPeriod(final XiaomingUser user,
                                 @CommandParameter("time") final String timeString) {
        final long time = TimeUtil.parseTime(timeString);
        if (time == -1) {
            user.sendMessage("{}并不是一个合理的时间哦", timeString);
        } else {
            final CallLimitConfig groupCallLimitConfig = callLimitManager.getGroupCallLimiter().getConfig();
            groupCallLimitConfig.setPeriod(time);
            getXiaomingBot().getConfig().readySave();
            user.sendMessage("成功设置群内召唤周期为{}，在这段时间内最多召唤{}次小明",
                    TimeUtil.toTimeString(time),
                    groupCallLimitConfig.getTop());
        }
    }

    @Command(CommandWordUtil.GROUP_REGEX + CALL_REGEX + CommandWordUtil.LIMIT_REGEX + " (上限|top) {time}")
    @RequirePermission("limit.group.top")
    public void onSetGroupTop(final XiaomingUser user,
                              @CommandParameter("time") final String timeString) {
        if (!timeString.matches("\\d+")) {
            user.sendError("{}并不是一个合理的数字哦", timeString);
        }
        final CallLimitConfig groupCallLimitConfig = callLimitManager.getGroupCallLimiter().getConfig();
        int time = Integer.parseInt(timeString);
        groupCallLimitConfig.setTop(time);
        getXiaomingBot().getConfig().readySave();
        callLimitManager.getGroupCallLimiter().getRecords().clear();
        user.sendMessage("成功设置群内召唤上限为{}次，已清空所有纪录。未来每{}内最多召唤这么多次小明",
                time,
                TimeUtil.toTimeString(groupCallLimitConfig.getPeriod()));
    }

    @Command(CommandWordUtil.GROUP_REGEX + CALL_REGEX + CommandWordUtil.LIMIT_REGEX + " (冷却时间|冷却|cooldown) {time}")
    @RequirePermission("limit.group.cooldown")
    public void onSetGroupCoolDown(final XiaomingUser user,
                                   @CommandParameter("time") final String timeString) {
        if (!timeString.matches("\\d+")) {
            user.sendError("{}并不是一个合理的数字哦", timeString);
        }
        int time = Integer.parseInt(timeString);
        if (time > TimeUtil.SECOND_MINS * 30) {
            user.sendError("过长的召唤冷却时间会影响使用体验");
        }
        final CallLimitConfig config = callLimitManager.getGroupCallLimiter().getConfig();
        config.setCoolDown(time);
        getXiaomingBot().getConfig().readySave();
        user.sendMessage("成功设置小明的召唤冷却时间为{}", TimeUtil.toTimeString(config.getPeriod()));
    }

    @Command(CommandWordUtil.PRIVATE_REGEX + CALL_REGEX + CommandWordUtil.LIMIT_REGEX)
    public void onLookPrivateCallLimit(final QQXiaomingUser user) {
        final CallLimitConfig config = callLimitManager.getPrivateCallLimiter().getConfig();
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

    @Command(CommandWordUtil.PRIVATE_REGEX + CALL_REGEX + CommandWordUtil.LIMIT_REGEX + " (周期|period) {time}")
    @RequirePermission("limit.private.period")
    public void onSetPrivatePeriod(final XiaomingUser user,
                                 @CommandParameter("time") final String timeString) {
        final long time = TimeUtil.parseTime(timeString);
        if (time == -1) {
            user.sendMessage("{}并不是一个合理的时间哦", timeString);
        } else {
            final CallLimitConfig config = callLimitManager.getPrivateCallLimiter().getConfig();
            config.setPeriod(time);
            getXiaomingBot().getConfig().readySave();
            user.sendMessage("成功设置私聊召唤周期为{}，在这段时间内最多召唤{}次小明",
                    TimeUtil.toTimeString(time),
                    config.getTop());
        }
    }

    @Command(CommandWordUtil.PRIVATE_REGEX + CALL_REGEX + CommandWordUtil.LIMIT_REGEX + " (上限|top) {time}")
    @RequirePermission("limit.private.top")
    public void onSetPrivateTop(final XiaomingUser user,
                              @CommandParameter("time") final String timeString) {
        if (!timeString.matches("\\d+")) {
            user.sendError("{}并不是一个合理的数字哦", timeString);
        }
        final CallLimitConfig config = callLimitManager.getPrivateCallLimiter().getConfig();
        int time = Integer.parseInt(timeString);
        config.setTop(time);
        getXiaomingBot().getConfig().readySave();
        callLimitManager.getPrivateCallLimiter().getRecords().clear();
        user.sendMessage("成功设置私聊召唤上限为{}次，已清空所有纪录。未来每{}内最多召唤这么多次小明",
                time,
                TimeUtil.toTimeString(config.getPeriod()));
    }

    @Command(CommandWordUtil.PRIVATE_REGEX + CALL_REGEX + CommandWordUtil.LIMIT_REGEX + " (冷却时间|冷却|cooldown) {time}")
    @RequirePermission("limit.private.cooldown")
    public void onSetPrivateCoolDown(final XiaomingUser user,
                                   @CommandParameter("time") final String timeString) {
        if (!timeString.matches("\\d+")) {
            user.sendError("{}并不是一个合理的数字哦", timeString);
        }
        int time = Integer.parseInt(timeString);
        if (time > TimeUtil.SECOND_MINS * 30) {
            user.sendError("过长的召唤冷却时间会影响使用体验");
        }
        final CallLimitConfig config = callLimitManager.getPrivateCallLimiter().getConfig();
        config.setCoolDown(time);
        getXiaomingBot().getConfig().readySave();
        user.sendMessage("成功设置小明的私聊召唤冷却时间为{}", TimeUtil.toTimeString(config.getPeriod()));
    }
}