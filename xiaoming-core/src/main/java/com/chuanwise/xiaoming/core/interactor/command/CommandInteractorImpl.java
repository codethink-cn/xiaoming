package com.chuanwise.xiaoming.core.interactor.command;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.interactor.command.CommandInteractor;
import com.chuanwise.xiaoming.api.interactor.filter.FilterMatcher;
import com.chuanwise.xiaoming.api.interactor.filter.ParameterFilterMatcher;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.StringUtils;
import com.chuanwise.xiaoming.core.interactor.InteractorImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Method;

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
        return !StringUtils.isEmpty(usageCommandHead);
    }

    @Override
    public void initialize() {
        // 注册指令格式指令
        if (isEnableUsageCommand()) {
            try {
                final String format = getUsageCommandHead() + CommandWords.HELP;
                register(getClass().getMethod("onUsage", XiaomingUser.class),
                        new String[0],
                        new FilterMatcher[]{ new ParameterFilterMatcher(format) },
                        new String[]{ StringUtils.translateUsageRegex(format) },
                        false,
                        false);
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
