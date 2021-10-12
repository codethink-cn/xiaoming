package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.TimeUtil;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.report.ReportMessage;
import cn.chuanwise.xiaoming.report.ReportMessageImpl;
import cn.chuanwise.xiaoming.report.ReportMessageManager;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;

import java.util.List;
import java.util.Objects;

public class ReportInteractors extends SimpleInteractors {
    ReportMessageManager reportMessageManager;
    List<ReportMessage> reportMessages;

    @Override
    public void onRegister() {
        reportMessageManager = xiaomingBot.getReportMessageManager();
        reportMessages = reportMessageManager.getReportMessages();
    }

    public void showReportMessage(XiaomingUser user,
                                  ReportMessage message) {
        StringBuilder builder = new StringBuilder("【报告详情】").append("\n");
        builder.append(message.getMessage()).append("\n")
                .append("QQ：" + message.getCode());

        if (Objects.nonNull(message.getInput())) {
            builder.append("\n").append("输入：" + message.getInput());
        }

        if (message.getGroup() != 0) {
            builder.append("\n").append("群：" + message.getGroup());
        }

        builder.append("\n").append("时间：" + TimeUtil.format(message.getTime()));

        user.sendPrivateMessage(builder.toString());
    }

    @Filter("反馈")
    public void onReport(XiaomingUser user) {
        user.sendMessage("你遇到了什么问题，或有什么建议呢？赶快告诉小明吧，" +
                "当你说完了，告诉我「结束」就可以啦 {lang.happy}");

        StringBuilder builder = new StringBuilder();
        String nextInput = user.nextMessageOrExit().serialize();
        while (true) {
            if (Objects.equals(nextInput, "结束")){
                if (builder.length() == 0) {
                    user.sendMessage("本次没有反馈任何信息哦");
                } else {
                    final ReportMessageManager reportMessageManager = getXiaomingBot().getReportMessageManager();
                    reportMessageManager.addMessage(new ReportMessageImpl(user.getCode(), builder.toString()));
                    getXiaomingBot().getFileSaver().readyToSave(reportMessageManager);

                    user.sendMessage("感谢你的反馈，一起期待更好的小明吧 {lang.happy}");
                    getXiaomingBot().getContactManager().sendGroupMessage("log", "收到一则用户反馈");
                }
                return;
            } else {
                if (builder.length() == 0) {
                    builder.append(nextInput);
                } else {
                    builder.append("\n").append(nextInput);
                }
            }
            nextInput = user.nextMessageOrExit().serialize();
        }
    }

    @Filter(CommandWords.RECENT + CommandWords.REPORT)
    @Permission("report.look")
    public void onLookLastMessage(XiaomingUser user) {
        if (reportMessages.isEmpty()) {
            user.sendMessage("没有未经查看的报告哦");
        } else if (reportMessages.size() == 1) {
            showReportMessage(user, reportMessages.get(0));
            reportMessages.clear();
            getXiaomingBot().getFileSaver().readyToSave(reportMessageManager);
        } else {
            user.sendMessage("一共有 " + reportMessages.size() + " 个未经查看的报告：\n" +
                    CollectionUtil.toIndexString(reportMessages, ReportMessage::getMessage));
        }
    }

    @Filter(CommandWords.RECENT + CommandWords.REPORT + " {序号}")
    @Permission("report.look")
    public void onLookMessage(XiaomingUser user,
                              @FilterParameter("序号") ReportMessage reportMessage) {
        user.sendMessage("{lang.reportDetail}", reportMessage);
    }

//    @Filter(CommandWords.CLEAR + CommandWords.RECENT + CommandWords.REPORT)
//    @Permission("report.clear")
//    public void onClearMessage(XiaomingUser user) {
//        if (reportMessages.isEmpty()) {
//            user.sendMessage("并没有需要清除的未经查看的报告哦");
//        } else {
//            reportMessages.clear();
//            getXiaomingBot().getFileSaver().readyToSave(reportMessageManager);
//            user.sendMessage("成功清除未经查看的报告");
//        }
//    }
}