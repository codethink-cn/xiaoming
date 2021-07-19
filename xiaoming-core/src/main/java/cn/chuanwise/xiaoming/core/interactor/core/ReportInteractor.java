package cn.chuanwise.xiaoming.core.interactor.core;

import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.TimeUtility;
import cn.chuanwise.xiaoming.api.annotation.Filter;
import cn.chuanwise.xiaoming.api.annotation.FilterParameter;
import cn.chuanwise.xiaoming.api.annotation.Permission;
import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.error.ReportMessage;
import cn.chuanwise.xiaoming.api.error.ReportMessageManager;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;
import cn.chuanwise.xiaoming.api.utility.CommandWords;
import cn.chuanwise.xiaoming.core.interactor.InteractorImpl;
import cn.chuanwise.xiaoming.core.report.ReportMessageImpl;

import java.util.List;
import java.util.Objects;

public class ReportInteractor extends InteractorImpl {
    final ReportMessageManager reportMessageManager;
    final List<ReportMessage> reportMessages;

    static final String REPORT = "(报告|report)";

    public ReportInteractor(XiaomingBot xiaomingBot) {
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
            builder.append("\n").append("输入：" + CollectionUtility.toString(message.getLastInputs(), "、"));
        }

        if (message.getGroup() != 0) {
            builder.append("\n").append("群：" + message.getGroup());
        }

        builder.append("\n").append("时间：" + TimeUtility.format(message.getTime()));

        user.sendPrivateMessage(builder.toString());
    }

    @Filter("反馈")
    public void onReport(XiaomingUser user) {
        user.sendMessage("你遇到了什么问题，或有什么建议呢？赶快告诉小明吧，" +
                "当你说完了，告诉我「结束」就可以啦 {happy}");

        StringBuilder builder = new StringBuilder();
        String nextInput = user.nextInput().serialize();
        while (true) {
            if (Objects.equals(nextInput, "结束")){
                if (builder.length() == 0) {
                    user.sendMessage("本次没有反馈任何信息哦");
                } else {
                    final ReportMessageManager reportMessageManager = getXiaomingBot().getReportMessageManager();
                    reportMessageManager.addMessage(new ReportMessageImpl(user.getCode(), builder.toString()));
                    getXiaomingBot().getScheduler().readySave(reportMessageManager);

                    user.sendMessage("感谢你的反馈，一起期待更好的小明吧 {happy}");
                    getXiaomingBot().getGroupRecordManager().sendMessageToTaggedGroup("log", "收到一则用户反馈");
                }
                return;
            } else {
                if (builder.length() == 0) {
                    builder.append(nextInput);
                } else {
                    builder.append("\n").append(nextInput);
                }
            }
            nextInput = user.nextInput().serialize();
        }
    }

    @Filter(CommandWords.RECENT + REPORT)
    @Permission("message.look")
    public void onLookLastMessage(XiaomingUser user) {
        if (reportMessages.isEmpty()) {
            user.sendMessage("没有未经查看的报告哦");
        } else if (reportMessages.size() == 1) {
            showReportMessage(user, reportMessages.get(0));
            reportMessages.clear();
            getXiaomingBot().getScheduler().readySave(reportMessageManager);
        } else {
            user.sendMessage("一共有 " + reportMessages.size() + " 个未经查看的报告：\n" +
                    CollectionUtility.toIndexString(reportMessages, ReportMessage::getMessage));
        }
    }

    @Filter(CommandWords.RECENT + REPORT + " {index}")
    @Permission("message.look")
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
    @Permission("message.clear")
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