package com.taixue.xiaoming.bot.host.hook;

import com.taixue.xiaoming.bot.api.data.RegularSaveDataManager;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.ConsoleDispatcherUser;
import com.taixue.xiaoming.bot.api.plugin.PluginManager;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import com.taixue.xiaoming.bot.host.XiaomingLauncher;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ShutdownHook extends HostObjectImpl implements Runnable {
    volatile XiaomingUser user;

    public void setUser(XiaomingUser user) {
        this.user = user;
    }

    @Override
    public void run() {
        final XiaomingLauncher instance = XiaomingLauncher.getInstance();
        if (Objects.isNull(user)) {
            user = instance.getConsoleXiaomingUser();
        }

        final ConsoleDispatcherUser consoleXiaomingUser = instance.getConsoleXiaomingUser();
        user.sendMessage("开始关闭小明，请不要立即终止程序，否则数据可能会丢失");

        consoleXiaomingUser.setMessage("正在关闭进程池");
        final ExecutorService service = instance.getXiaomingBot().getService();
        service.shutdown(); // Disable new tasks from being submitted
        // 设定最大重试次数
        try {
            // 等待 60 s
            if (!service.awaitTermination(5, TimeUnit.SECONDS)) {
                // 调用 shutdownNow 取消正在执行的任务
                service.shutdownNow();
                // 再次等待 60 s，如果还未结束，可以再次尝试，或者直接放弃
                if (!service.awaitTermination(5, TimeUnit.SECONDS)) {
                    consoleXiaomingUser.sendError("线程池任务未正常执行结束");
                }
            }
        } catch (InterruptedException exception) {
            // 重新调用 shutdownNow
            service.shutdownNow();
        }

        consoleXiaomingUser.setMessage("正在保存文件");
        final RegularSaveDataManager regularSaveDataManager = getXiaomingBot().getRegularSaveDataManager();
        if (regularSaveDataManager.getSaveSet().isEmpty()) {
            consoleXiaomingUser.sendMessage("没有任何需要保存的数据");
        } else {
            consoleXiaomingUser.sendMessage("正在保存数据");
            regularSaveDataManager.save(consoleXiaomingUser);
        }

        consoleXiaomingUser.sendMessage("正在卸载所有插件");
        final PluginManager pluginManager = getXiaomingBot().getPluginManager();
        for (XiaomingPlugin plugin : pluginManager.getLoadedPlugins()) {
            try {
                pluginManager.unloadPlugin(consoleXiaomingUser, plugin);
            } catch (Exception exception) {
                consoleXiaomingUser.sendError("卸载插件时出现异常：{}", exception);
                exception.printStackTrace();
            }
        }

        user.sendMessage("关闭完成");
    }
}