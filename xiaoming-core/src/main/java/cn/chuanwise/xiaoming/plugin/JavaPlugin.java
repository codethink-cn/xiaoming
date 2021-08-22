package cn.chuanwise.xiaoming.plugin;

import cn.chuanwise.exception.UnsupportedVersionException;
import cn.chuanwise.utility.CheckUtility;
import cn.chuanwise.utility.ObjectUtility;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;

@Getter
public class JavaPlugin implements Plugin {
    private volatile Status status = Status.CONSTRUCTED;
    private final Object statusConditionalVariable = new Object();

    @Setter
    protected XiaomingBot xiaomingBot;

    protected PluginHandler handler;

    @Override
    public void setHandler(PluginHandler handler) {
        this.handler = handler;
        handler.setPlugin(this);
    }

    @Setter
    @NonNull
    Logger logger;

    @Setter
    @NonNull
    File dataFolder;

    @Override
    public void setStatus(Status status) {
        this.status = status;

        // 唤醒状态等待者
        synchronized (statusConditionalVariable) {
            statusConditionalVariable.notifyAll();
        }
    }

    @Override
    public Status nextStatus(long timeout) throws InterruptedException {
        switch (ObjectUtility.wait(statusConditionalVariable, timeout)) {
            case NOTIFY:
                return status;
            case TIMEOUT:
                return null;
            case INTERRUPT:
                throw new InterruptedException();
            default:
                throw new UnsupportedVersionException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (Objects.isNull(o) || !(o instanceof JavaPlugin)) {
            return false;
        }
        final JavaPlugin javaPlugin = (JavaPlugin) o;
        return Objects.equals(getName(), javaPlugin.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}