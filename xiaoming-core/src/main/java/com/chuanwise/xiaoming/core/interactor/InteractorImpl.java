package com.chuanwise.xiaoming.core.interactor;

import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.interactor.detail.InteractorMethodDetail;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.core.object.PluginObjectImpl;
import com.chuanwise.xiaoming.core.object.XiaomingObjectImpl;
import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 交互器标准实现
 * @author Chuanwise
 */
@Data
public abstract class InteractorImpl extends PluginObjectImpl implements Interactor {
    XiaomingPlugin plugin;

    String name;

    Set<InteractorMethodDetail> methodDetails = new HashSet<>();

    @Override
    public String getName() {
        final StringBuilder builder = new StringBuilder();
        final XiaomingPlugin plugin = getPlugin();
        if (Objects.nonNull(plugin)) {
            builder.append(plugin.getAlias());
        } else {
            builder.append("内核");
        }
        builder.append(" ").append(Objects.nonNull(name) ? name : getClass().getSimpleName());
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(getClass(), obj.getClass());
    }

    @Override
    public void setPlugin(XiaomingPlugin plugin) {
        this.plugin = plugin;
        setXiaomingBot(plugin.getXiaomingBot());
    }
}