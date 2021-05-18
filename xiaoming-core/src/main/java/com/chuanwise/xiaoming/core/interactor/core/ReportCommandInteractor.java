package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.error.ReportMessage;
import com.chuanwise.xiaoming.api.error.ReportMessageManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.TimeUtil;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;

import java.util.List;
import java.util.Objects;

public class ReportCommandInteractor extends CommandInteractorImpl {
    final ReportMessageManager reportMessageManager;
    final List<ReportMessage> reportMessages;

    public ReportCommandInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        reportMessageManager = getXiaomingBot().getReportMessageManager();
        reportMessages = reportMessageManager.getReportMessages();
    }

    public void onShowErrorMessage(XiaomingUser user,
                                   ReportMessage message) {
        StringBuilder builder = new StringBuilder("【消息详情】").append("\n");
        builder.append(message.getMessage()).append("\n")
                .append("QQ：" + message.getQq());

        if (Objects.nonNull(message.getLastInputs())) {
            builder.append("输入：" + message.getLastInputs());
        }

        if (message.getGroup() != 0) {
            builder.append("群：" + message.getGroup());
        }

        builder.append("时间：" + TimeUtil.FORMAT.format(message.getTime()));

        user.sendPrivateMessage(builder.toString());
    }

    @Filter(CommandWords.RECENT_REGEX + CommandWords.MESSAGE_REGEX)
    @RequirePermission("message.look")
    public void onLookLastMessage(XiaomingUser user) {
        if (reportMessages.isEmpty()) {
            user.sendMessage("没有未经查看的消息哦");
        } else if (reportMessages.size() == 1) {
            onShowErrorMessage(user, reportMessages.get(0));
            reportMessages.clear();
            getXiaomingBot().getRegularPreserveManager().readySave(reportMessageManager);
        } else {
            StringBuilder builder = new StringBuilder()
                    .append("一共有 ").append(reportMessages.size()).append(" 个未经查看的消息");

            int index = 1;
            for (ReportMessage reportMessage : reportMessages) {
                String shortMessage = reportMessage.getMessage();
                if (shortMessage.length() > 30) {
                    shortMessage = shortMessage.substring(0, 29) + "...";
                }
                builder.append("\n").append(index++).append("、").append(shortMessage);
            }
            user.sendMessage(builder.toString());
        }
    }

    @Filter(CommandWords.RECENT_REGEX + CommandWords.MESSAGE_REGEX + " {index}")
    @RequirePermission("message.look")
    public void onLookMessage(XiaomingUser user,
                                @FilterParameter("index") final String indexString) {
        if (reportMessages.isEmpty()) {
            user.sendMessage("没有未经查看的消息哦");
        } else if (reportMessages.size() == 1) {
            user.sendMessage("只有一个未经处理的消息，直接使用 #近期消息 就可以啦");
            return;
        }

        final int index;
        if (indexString.matches("\\d+")) {
            index = Integer.parseInt(indexString);
        } else {
            user.sendError("{}并不是一个合理的数字哦", indexString);
            return;
        }

        if (index <= 0 || index > reportMessages.size()) {
            user.sendError("{}不对哦，它应该是介于 1 到 {} 之间的数字", indexString, reportMessages.size());
        } else {
            final ReportMessage reportMessage = reportMessages.get(index - 1);
            onShowErrorMessage(user, reportMessage);
            reportMessages.remove(reportMessage);
            getXiaomingBot().getRegularPreserveManager().readySave(reportMessageManager);
        }
    }

    @Filter(CommandWords.CLEAR_REGEX + CommandWords.RECENT_REGEX + CommandWords.MESSAGE_REGEX)
    @RequirePermission("message.clear")
    public void onClearMessage(XiaomingUser user) {
        if (reportMessages.isEmpty()) {
            user.sendMessage("并没有需要清除的未经查看的消息哦");
        } else {
            reportMessages.clear();
            getXiaomingBot().getRegularPreserveManager().readySave(reportMessageManager);
            user.sendMessage("成功清除未经查看的消息");
        }
    }
}