package cn.chuanwise.xiaoming.interactor;

import cn.chuanwise.utility.StringUtility;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.object.PluginObjectImpl;
import cn.chuanwise.xiaoming.plugin.XiaomingPlugin;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 交互器标准实现
 * @author Chuanwise
 */
@Data
public abstract class InteractorImpl extends PluginObjectImpl implements Interactor {
    String usageCommandFormat = null;

    Set<InteractorMethodInformation> interactorMethodInformation = new HashSet<>();

    Map<String, InteractorMethodInformation> customizer;

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
        if (StringUtility.nonEmpty(usageCommandFormat)) {
            try {
                final String format = usageCommandFormat;
                register(getClass().getMethod("onUsage", XiaomingUser.class),
                        new InteractorMethodInformation(new String[]{usageCommandFormat}, new String[0], new String[0], new String[0], false, false, false));
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