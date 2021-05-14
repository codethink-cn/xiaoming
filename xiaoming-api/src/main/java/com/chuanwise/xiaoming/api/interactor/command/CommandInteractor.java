package com.chuanwise.xiaoming.api.interactor.command;

import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.interactor.detail.InteractorMethodDetail;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.StringUtil;

import java.util.*;

public interface CommandInteractor extends Interactor {

    boolean isEnableUsageCommand();

    void enableUsageCommand(String usageCommandHead);

    void disableUsageCommand(String usageCommandHead);

    String getUsageCommandHead();
}
