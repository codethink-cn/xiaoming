package com.chuanwise.xiaoming.api.interactor.command;

import com.chuanwise.xiaoming.api.interactor.Interactor;

public interface CommandInteractor extends Interactor {
    boolean isEnableUsageCommand();

    void enableUsageCommand(String usageCommandHead);

    void disableUsageCommand(String usageCommandHead);

    String getUsageCommandHead();
}
