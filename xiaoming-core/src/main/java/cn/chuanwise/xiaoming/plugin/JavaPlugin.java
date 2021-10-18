package cn.chuanwise.xiaoming.plugin;

import cn.chuanwise.api.SimpleSetableStatusHolder;
import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.object.PluginObject;
import cn.chuanwise.xiaoming.preservable.SimplePreservable;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Getter
public class JavaPlugin
        extends SimpleSetableStatusHolder<Plugin.Status>
        implements Plugin {
    @Setter
    protected XiaomingBot xiaomingBot;

    protected PluginHandler handler;

    public JavaPlugin() {
        super(Status.LOADED);
    }

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
    public <T extends Preservable> T loadFileAs(Class<T> clazz, File file) throws IOException {
        final T result = getXiaomingBot().getFileLoader().load(clazz, file);
        if (result instanceof PluginObject) {
            ((PluginObject) result).setPlugin(this);
        }
        return result;
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