package cn.chuanwise.xiaoming.debug;

import cn.chuanwise.toolkit.serialize.serializer.Serializer;
import cn.chuanwise.util.ConditionUtil;
import cn.chuanwise.util.MessageDigestUtil;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.bot.XiaomingBotImpl;
import cn.chuanwise.xiaoming.launcher.SimpleXiaomingLauncher;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.plugin.PluginHandler;
import cn.chuanwise.xiaoming.plugin.PluginHandlerImpl;
import cn.chuanwise.util.SerializerUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PluginDebuggerBuilder {
    final Serializer serializer = SerializerUtil.initializedSerializer();
    final List<PluginHandler> pluginHandlers = new ArrayList<>();

    long code;
    byte[] passwordMd5;
    File workingDirectory;

    public PluginDebugger build() {
        ConditionUtil.notNull(passwordMd5, "password");
        final XiaomingBot xiaomingBot = new XiaomingBotImpl(code, passwordMd5);
        if (Objects.nonNull(workingDirectory)) {
            xiaomingBot.setWorkingDirectory(workingDirectory);
            xiaomingBot.getMiraiBot().getConfiguration().setWorkingDir(new File(workingDirectory, "launcher"));
        } else {
            xiaomingBot.getMiraiBot().getConfiguration().setWorkingDir(new File("launcher"));
        }
        xiaomingBot.getMiraiBot().getConfiguration().fileBasedDeviceInfo();
        return new SimplePluginDebugger(new SimpleXiaomingLauncher(xiaomingBot), pluginHandlers);
    }

    public PluginDebuggerBuilder code(long code) {
        this.code = code;
        return this;
    }

    public PluginDebuggerBuilder password(String password) {
        passwordMd5 = MessageDigestUtil.MD5.digest(password.getBytes());
        return this;
    }

    public PluginDebuggerBuilder md5(byte[] passwordMd5) {
        this.passwordMd5 = passwordMd5;
        return this;
    }

    public PluginDebuggerBuilder addPlugin(File file) throws IOException {
        final PluginHandler handler = serializer.deserialize(file, "UTF-8", PluginHandlerImpl.class);
        pluginHandlers.add(handler);
        return this;
    }

    public PluginDebuggerBuilder addPlugin(InputStream inputStream) throws IOException {
        final PluginHandler handler = serializer.deserialize(inputStream, "UTF-8", PluginHandlerImpl.class);
        pluginHandlers.add(handler);
        return this;
    }

    public PluginDebuggerBuilder addPlugin(PluginHandler pluginHandler) {
        pluginHandlers.add(pluginHandler);
        return this;
    }

    public PluginDebuggerBuilder workingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
        return this;
    }

    public <T extends Plugin> PluginDebuggerBuilder addPlugin(String name, Class<T> mainClass) {
        final PluginHandler pluginHandler = new PluginHandlerImpl();
        pluginHandler.set("name", name);
        pluginHandler.set("main", mainClass.getName());
        return addPlugin(pluginHandler);
    }

    public <T extends Plugin> PluginDebuggerBuilder addPlugin(Class<T> mainClass) {
        return addPlugin(mainClass.getSimpleName(), mainClass);
    }
}
