package cn.chuanwise.xiaoming.debug;

import cn.chuanwise.toolkit.serialize.serializer.Serializer;
import cn.chuanwise.utility.CheckUtility;
import cn.chuanwise.utility.MessageDigestUtility;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.bot.XiaomingBotImpl;
import cn.chuanwise.xiaoming.launcher.SimpleXiaomingLauncher;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.plugin.PluginHandler;
import cn.chuanwise.xiaoming.plugin.PluginHandlerImpl;
import cn.chuanwise.xiaoming.utility.SerializerUtility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class XiaomingDebuggerBuilder {
    final Serializer serializer = SerializerUtility.initializedSerializer();
    final List<PluginHandler> pluginHandlers = new ArrayList<>();

    long code;
    byte[] passwordMd5;
    File workingDirectory;

    public XiaomingDebugger build() {
        CheckUtility.nonNull(passwordMd5, "password");
        final XiaomingBot xiaomingBot = new XiaomingBotImpl(code, passwordMd5);
        if (Objects.nonNull(workingDirectory)) {
            xiaomingBot.setWorkingDirectory(workingDirectory);
            xiaomingBot.getMiraiBot().getConfiguration().setWorkingDir(new File(workingDirectory, "launcher"));
        } else {
            xiaomingBot.getMiraiBot().getConfiguration().setWorkingDir(new File("launcher"));
        }
        return new SimpleXiaomingDebugger(new SimpleXiaomingLauncher(xiaomingBot), pluginHandlers);
    }

    public XiaomingDebuggerBuilder code(long code) {
        this.code = code;
        return this;
    }

    public XiaomingDebuggerBuilder password(String password) {
        passwordMd5 = MessageDigestUtility.MD5.digest(password.getBytes());
        return this;
    }

    public XiaomingDebuggerBuilder md5(byte[] passwordMd5) {
        this.passwordMd5 = passwordMd5;
        return this;
    }

    public XiaomingDebuggerBuilder addPlugin(File file) throws IOException {
        final PluginHandler handler = serializer.deserialize(file, "UTF-8", PluginHandlerImpl.class);
        pluginHandlers.add(handler);
        return this;
    }

    public XiaomingDebuggerBuilder addPlugin(InputStream inputStream) throws IOException {
        final PluginHandler handler = serializer.deserialize(inputStream, "UTF-8", PluginHandlerImpl.class);
        pluginHandlers.add(handler);
        return this;
    }

    public XiaomingDebuggerBuilder addPlugin(PluginHandler pluginHandler) {
        pluginHandlers.add(pluginHandler);
        return this;
    }

    public XiaomingDebuggerBuilder workingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
        return this;
    }

    public <T extends Plugin> XiaomingDebuggerBuilder addPlugin(String name, Class<T> mainClass) {
        final PluginHandler pluginHandler = new PluginHandlerImpl();
        pluginHandler.set("name", name);
        pluginHandler.set("main", mainClass.getName());
        return addPlugin(pluginHandler);
    }

    public <T extends Plugin> XiaomingDebuggerBuilder addPlugin(String name, T instance) {
        return addPlugin(name, instance.getClass());
    }
}
