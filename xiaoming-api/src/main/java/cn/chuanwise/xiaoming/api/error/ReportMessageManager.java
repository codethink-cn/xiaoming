package cn.chuanwise.xiaoming.api.error;

import cn.chuanwise.xiaoming.api.object.ModuleObject;
import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;

import java.io.File;
import java.util.List;

public interface ReportMessageManager extends ModuleObject, Preservable<File> {
    void addMessage(ReportMessage reportMessage);

    void addThrowableMessage(XiaomingUser user, Throwable throwable);

    void addThrowableMessage(Throwable throwable);

    List<ReportMessage> getReportMessages();
}
