package com.chuanwise.xiaoming.api.error;

import com.chuanwise.xiaoming.api.object.ModuleObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.io.File;
import java.util.List;

public interface ReportMessageManager extends ModuleObject, Preservable<File> {
    void addMessage(ReportMessage reportMessage);

    void addThrowableMessage(XiaomingUser user, Throwable throwable);

    void addThrowableMessage(Throwable throwable);

    List<ReportMessage> getReportMessages();
}
