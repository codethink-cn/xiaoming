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
import java.util.Set;

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
    @SuppressWarnings("unchecked")
    public <T extends Preservable> T loadFileAs(Class<T> clazz, File file) throws IOException {
        final T result = getXiaomingBot().getFileLoader().load(clazz, file);
        if (result instanceof PluginObject) {
            ((PluginObject) result).setPlugin(this);
            ((PluginObject<?>) result).setXiaomingBot(xiaomingBot);
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

    @Override
    public Set<String> getOriginalTags() {
        return handler.getOriginalTags();
    }

    @Override
    public void flush() {
        handler.flush();
    }

    @Override
    public Set<String> getTags() {
        return handler.getTags();
    }

    @Override
    public boolean addTag(String tag) {
        return handler.addTag(tag);
    }

    @Override
    public boolean hasTag(String tag) {
        return handler.hasTag(tag);
    }

    @Override
    public boolean removeTag(String tag) {
        return handler.removeTag(tag);
    }
}