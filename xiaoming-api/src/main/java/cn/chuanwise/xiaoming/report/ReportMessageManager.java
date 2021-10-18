package cn.chuanwise.xiaoming.report;

import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.xiaoming.user.XiaomingUser;

import java.util.List;

public interface ReportMessageManager extends ModuleObject, Preservable {
    void addMessage(ReportMessage reportMessage);

    void addThrowableMessage(XiaomingUser user, Throwable throwable);

    void addThrowableMessage(Throwable throwable);

    List<ReportMessage> getReportMessages();
}
