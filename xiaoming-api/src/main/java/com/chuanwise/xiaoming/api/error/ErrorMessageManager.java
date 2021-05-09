package com.chuanwise.xiaoming.api.error;

import com.chuanwise.xiaoming.api.object.HostXiaomingObject;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.io.File;

public interface ErrorMessageManager extends HostXiaomingObject, Preservable<File> {
    void addErrorMessage(ErrorMessage errorMessage);

    void addGroupThrowableMessage(GroupXiaomingUser user, Throwable throwable);

    void addThrowableMessage(XiaomingUser user, Throwable throwable);

    java.util.List<ErrorMessage> getErrorMessages();

    void setErrorMessages(java.util.List<ErrorMessage> errorMessages);
}
