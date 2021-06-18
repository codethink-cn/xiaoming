package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.Require;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.error.ReportMessage;
import com.chuanwise.xiaoming.api.error.ReportMessageManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CollectionUtils;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.TimeUtils;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;

import java.util.List;
import java.util.Objects;

public class ReportCommandInteractor extends CommandInteractorImpl {
    final ReportMessageManager reportMessageManager;
    final List<ReportMessage> reportMessages;

    static final String REPORT = "(报告|report)";

    public ReportCommandInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        reportMessageManager = getXiaomingBot().getReportMessageManager();
        reportMessages = reportMessageManager.getReportMessages();
    }

    public void showReportMessage(XiaomingUser user,
                                  ReportMessage message) {
        StringBuilder builder = new StringBuilder("【报告详情】").append("\n");
        builder.append(message.getMessage()).append("\n")
                .append("QQ：" + message.getQq());

        if (Objects.nonNull(message.getLastInputs())) {
            builder.append("\n").append("输入：" + CollectionUtils.getSummary(message.getLastInputs(), String::toString, "", "（空）", "、"));
        }

        if (message.getGroup() != 0) {
            builder.append("\n").append("群：" + message.getGroup());
        }

        builder.append("\n").append("时间：" + TimeUtils.FORMAT.format(message.getTime()));

        user.sendPrivateMessage(builder.toString());
    }

    @Filter(CommandWords.RECENT + REPORT)
    @Require("message.look")
    public void onLookLastMessage(XiaomingUser user) {
        if (reportMessages.isEmpty()) {
            user.sendMessage("没有未经查看的报告哦");
        } else if (reportMessages.size() == 1) {
            showReportMessage(user, reportMessages.get(0));
            reportMessages.clear();
            getXiaomingBot().getScheduler().readySave(reportMessageManager);
        } else {
            user.sendMessage("一共有 " + reportMessages.size() + " 个未经查看的报告：\n" +
                    CollectionUtils.getIndexSummary(reportMessages, message -> {
                        return message.getMessage();
                    }));
        }
    }

    @Filter(CommandWords.RECENT + REPORT + " {index}")
    @Require("message.look")
    public void onLookMessage(XiaomingUser user,
                                @FilterParameter("index") final String indexString) {
        if (reportMessages.isEmpty()) {
            user.sendMessage("没有未经查看的报告哦");
        } else if (reportMessages.size() == 1) {
            user.sendMessage("只有一个未经处理的报告，直接使用「近期报告」就可以啦");
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
            showReportMessage(user, reportMessage);
            reportMessages.remove(reportMessage);
            getXiaomingBot().getScheduler().readySave(reportMessageManager);
        }
    }

    @Filter(CommandWords.CLEAR + CommandWords.RECENT + REPORT)
    @Require("message.clear")
    public void onClearMessage(XiaomingUser user) {
        if (reportMessages.isEmpty()) {
            user.sendMessage("并没有需要清除的未经查看的报告哦");
        } else {
            reportMessages.clear();
            getXiaomingBot().getScheduler().readySave(reportMessageManager);
            user.sendMessage("成功清除未经查看的报告");
        }
    }
}