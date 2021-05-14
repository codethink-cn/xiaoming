package com.chuanwise.xiaoming.core.interactor.command;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.interactor.command.CommandInteractor;
import com.chuanwise.xiaoming.api.interactor.detail.InteractorMethodDetail;
import com.chuanwise.xiaoming.api.interactor.filter.FilterMatcher;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.ArrayUtils;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.StringUtil;
import com.chuanwise.xiaoming.core.interactor.InteractorImpl;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.*;

@Getter
public class CommandInteractorImpl extends InteractorImpl implements CommandInteractor {
    String usageCommandHead = null;

    @Setter
    XiaomingPlugin plugin;

    @Override
    public void enableUsageCommand(String usageCommandHead) {
        this.usageCommandHead = usageCommandHead;
    }

    @Override
    public void disableUsageCommand(String usageCommandHead) {
        this.usageCommandHead = null;
    }

    @Override
    public boolean isEnableUsageCommand() {
        return !StringUtil.isEmpty(usageCommandHead);
    }

    @Override
    public void initialize() {
        // 注册指令格式指令
        if (isEnableUsageCommand()) {
            try {
                register(getClass().getMethod("showUsageStrings", XiaomingUser.class),
                        new String[]{ getUsageCommandHead() + " " + CommandWords.HELP_REGEX },
                        new FilterMatcher[0]);
            } catch (NoSuchMethodException exception) {
                exception.printStackTrace();
            }
        }

        // 对所有的指令，检查是否有 Filter 注解。如果有，则将其作为指令处理方法
        for (Method method : getClass().getMethods()) {
            register(method);
        }
    }
}
