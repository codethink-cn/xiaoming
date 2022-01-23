package cn.chuanwise.xiaoming.preservable;

import cn.chuanwise.toolkit.preservable.AbstractPreservable;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.object.PluginObject;
import cn.chuanwise.xiaoming.plugin.Plugin;

import java.beans.Transient;
import java.util.Optional;

public class SimplePreservable<T extends Plugin>
        extends AbstractPreservable
        implements PluginObject<T> {
    protected transient T plugin;
    protected transient XiaomingBot xiaomingBot;

    @Override
    @Transient
    public T getPlugin() {
        return plugin;
    }

    @Override
    public void setPlugin(T plugin) {
        this.plugin = plugin;
    }

    @Override
    @Transient
    public XiaomingBot getXiaomingBot() {
        return Optional.ofNullable(plugin)
                .map(Plugin::getXiaomingBot)
                .orElse(xiaomingBot);
    }

    @Override
    public void setXiaomingBot(XiaomingBot xiaomingBot) {
        this.xiaomingBot = xiaomingBot;
    }

    public void readyToSave() {
        xiaomingBot.getFileSaver().readyToSave(this);
    }
}