package com.chuanwise.xiaoming.api.error;

import com.chuanwise.xiaoming.api.object.HostObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.io.File;
import java.util.List;

public interface ErrorMessageManager extends HostObject, Preservable<File> {
    void addErrorMessage(ErrorMessage errorMessage);

    void addThrowableMessage(XiaomingUser user, Throwable throwable);

    void addThrowableMessage(Throwable throwable);

    List<ErrorMessage> getErrorMessages();
}
