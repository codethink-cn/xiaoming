package cn.chuanwise.xiaoming.core.interactor;

import cn.chuanwise.utility.StringUtility;
import cn.chuanwise.xiaoming.api.annotation.Filter;
import cn.chuanwise.xiaoming.api.command.CommandFormat;
import cn.chuanwise.xiaoming.api.interactor.Interactor;
import cn.chuanwise.xiaoming.api.interactor.detail.InteractorMethodDetail;
import cn.chuanwise.xiaoming.api.interactor.filter.FilterMatcher;
import cn.chuanwise.xiaoming.api.interactor.filter.ParameterFilterMatcher;
import cn.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;
import cn.chuanwise.xiaoming.api.utility.UsageStringUtility;
import cn.chuanwise.xiaoming.core.object.PluginObjectImpl;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 交互器标准实现
 * @author Chuanwise
 */
@Data
public abstract class InteractorImpl extends PluginObjectImpl implements Interactor {
    String usageCommandFormat = null;

    Set<InteractorMethodDetail> methodDetails = new HashSet<>();

    @Override
    public boolean equals(Object obj) {
        if (Objects.isNull(obj)) {
            return false;
        }
        return Objects.equals(getClass(), obj.getClass());
    }

    @Override
    public void setPlugin(XiaomingPlugin plugin) {
        this.plugin = plugin;
        setXiaomingBot(plugin.getXiaomingBot());
    }

    @Override
    public final void initialize() {
        // 注册指令格式指令
        if (!StringUtility.isEmpty(usageCommandFormat)) {
            try {
                final String format = usageCommandFormat;
                register(getClass().getMethod("onUsage", XiaomingUser.class),
                        new CommandFormat("onUsage",
                                new String[]{usageCommandFormat},
                                new String[0],
                                new String[]{UsageStringUtility.translateUsageRegex(format)},
                                false,
                                false,
                                false,
                                new String[0],
                                new String[0]));
            } catch (NoSuchMethodException exception) {
                getLog().error("没有找到交互方法：onUsage", exception);
            }
        }

        // 对所有的指令，检查是否有 Filter 注解。如果有，则将其作为指令处理方法
        for (Method method : getClass().getMethods()) {
            if (method.getAnnotationsByType(Filter.class).length != 0) {
                register(method);
            }
        }
    }
}