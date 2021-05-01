package com.taixue.xiaoming.bot.host.runnable;

import com.taixue.xiaoming.bot.api.annotation.RequirePermission;
import com.taixue.xiaoming.bot.api.command.executor.CommandExecutor;
import com.taixue.xiaoming.bot.api.annotation.Command;
import com.taixue.xiaoming.bot.api.command.executor.CommandManager;
import com.taixue.xiaoming.bot.api.annotation.CommandParameter;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.ConsoleDispatcherUser;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import com.taixue.xiaoming.bot.core.command.executor.CommandExecutorImpl;
import com.taixue.xiaoming.bot.core.user.XiaomingUserImpl;
import com.taixue.xiaoming.bot.host.XiaomingLauncher;
import com.taixue.xiaoming.bot.util.AtUtil;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Set;

public class ConsoleCommandRunnable extends HostObjectImpl implements Runnable {
    final String COMMAND_HEAD_REGEX = "(控制台|console|后台)";
    final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    volatile boolean running = true;

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void close() {
        setRunning(false);
    }

    @Override
    public void run() {
        final CommandManager commandManager = getXiaomingBot().getCommandManager();
        final ConsoleDispatcherUser consoleXiaomingUser = XiaomingLauncher.getInstance().getConsoleXiaomingUser();

        while (running) {
            try {
                if (!reader.ready()) {
                    continue;
                }
            } catch (IOException ignored) {
            }

            try {
                String input = reader.readLine();
                consoleXiaomingUser.setMessage(input);

                boolean executed = false;
                for (CommandExecutor coreCommandExecutor : commandManager.getCoreCommandExecutors()) {
                    if (coreCommandExecutor.onCommand(consoleXiaomingUser)) {
                        executed = true;
                        break;
                    }
                }
                for (Set<CommandExecutor> value : commandManager.getPluginCommandExecutors().values()) {
                    for (CommandExecutor commandExecutor : value) {
                        if (commandExecutor.onCommand(consoleXiaomingUser)) {
                            executed = true;
                            break;
                        }
                    }
                }
                if (!executed) {
                    getLogger().error("小明不知道你的意思 (；′⌒`)");
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}