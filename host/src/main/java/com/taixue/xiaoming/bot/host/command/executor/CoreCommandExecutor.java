package com.taixue.xiaoming.bot.host.command.executor;

import com.taixue.xiaoming.bot.api.annotation.CommandParameter;
import com.taixue.xiaoming.bot.api.annotation.RequirePermission;
import com.taixue.xiaoming.bot.api.command.executor.CommandExecutor;
import com.taixue.xiaoming.bot.api.annotation.Command;
import com.taixue.xiaoming.bot.api.command.executor.CommandManager;
import com.taixue.xiaoming.bot.api.config.Config;
import com.taixue.xiaoming.bot.api.data.RegularSaveDataManager;
import com.taixue.xiaoming.bot.api.exception.XiaomingRuntimeException;
import com.taixue.xiaoming.bot.api.listener.interactor.Interactor;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;
import com.taixue.xiaoming.bot.api.user.GroupXiaomingUser;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.command.executor.CommandExecutorImpl;
import com.taixue.xiaoming.bot.core.user.XiaomingUserImpl;
import com.taixue.xiaoming.bot.host.XiaomingLauncher;
import com.taixue.xiaoming.bot.util.AtUtil;
import com.taixue.xiaoming.bot.util.CommandWordUtil;
import love.forte.simbot.core.SimbotApp;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CoreCommandExecutor extends CommandExecutorImpl {
    public String getCommandExecutorsString() {
        StringBuilder builder = new StringBuilder();

        final CommandManager commandManager = getXiaomingBot().getCommandManager();
        final Set<CommandExecutor> coreCommandExecutors = commandManager.getCoreCommandExecutors();
        final Map<XiaomingPlugin, Set<CommandExecutor>> pluginCommandExecutors = commandManager.getPluginCommandExecutors();

        builder.append("内核指令处理器：");
        if (coreCommandExecutors.isEmpty()) {
            builder.append("（无）");
        } else {
            for (CommandExecutor coreCommandExecutor : coreCommandExecutors) {
                builder.append("\n").append(coreCommandExecutor.getClass().getName());
            }
        }
        builder.append("\n");

        builder.append("插件指令处理器：");
        if (pluginCommandExecutors.isEmpty()) {
            builder.append("（无）");
        } else {
            for (Map.Entry<XiaomingPlugin, Set<CommandExecutor>> entry : pluginCommandExecutors.entrySet()) {
                builder.append("\n")
                        .append("由").append(entry.getKey().getCompleteName()).append("注册：");
                for (CommandExecutor commandExecutor : entry.getValue()) {
                    builder.append("\n")
                            .append("> ").append(commandExecutor.getClass().getName());
                }
            }
        }
        return builder.toString();
    }

    public String getInteractorString() {
        StringBuilder builder = new StringBuilder();

        final Map<XiaomingPlugin, Set<Interactor>> pluginInteractors = getXiaomingBot().getInteractorManager().getPluginInteractors();
        builder.append("交互器：");
        if (pluginInteractors.isEmpty()) {
            builder.append("（无）");
        } else {
            for (Map.Entry<XiaomingPlugin, Set<Interactor>> entry : pluginInteractors.entrySet()) {
                builder.append("\n")
                        .append("由").append(entry.getKey().getCompleteName()).append("注册：");
                for (Interactor interactor : entry.getValue()) {
                    builder.append("\n")
                            .append("> ").append(interactor.getClass().getName());
                }
            }
        }
        return builder.toString();
    }

    public String getLoadedPluginString() {
        StringBuilder builder = new StringBuilder();

        final Set<XiaomingPlugin> loadedPlugins = getXiaomingBot().getPluginManager().getLoadedPlugins();
        builder.append("加载的插件：");
        if (loadedPlugins.isEmpty()) {
            builder.append("（无）");
        } else {
            for (XiaomingPlugin value : loadedPlugins) {
                builder.append("\n").append(value.getCompleteName());
            }
        }
        return builder.toString();
    }

    public String getPluginMessage(final XiaomingPlugin plugin) {
        StringBuilder builder = new StringBuilder();
        builder.append("插件：").append(plugin.getCompleteName());
        builder.append("\n");

        final Set<Interactor> interactors = getXiaomingBot().getInteractorManager().getInteractors(plugin);
        builder.append("交互器：");
        if (Objects.isNull(interactors) || interactors.isEmpty()) {
            builder.append("（无）");
        } else {
            for (Interactor interactor : interactors) {
                builder.append("\n")
                        .append(interactor.getClass().getName());
            }
        }
        builder.append("\n");

        final Set<CommandExecutor> commandExecutors = getXiaomingBot().getCommandManager().getPluginCommandExecutors(plugin);
        builder.append("指令处理器：");
        if (Objects.isNull(commandExecutors) || commandExecutors.isEmpty()) {
            builder.append("（无）");
        } else {
            for (CommandExecutor executor : commandExecutors) {
                builder.append("\n")
                        .append(executor.getClass().getName());
            }
        }

        return builder.toString();
    }

    @Command(CommandWordUtil.PLUGIN_REGEX)
    @RequirePermission("plugin.list")
    public void onLoadedPlugins(final XiaomingUser user) {
        user.sendMessage(getLoadedPluginString());
    }

    @Command(CommandWordUtil.PLUGIN_REGEX + " {plugin}")
    @RequirePermission("plugin.look")
    public void onLoadedPlugins(final XiaomingUser user,
                                @CommandParameter("plugin") final String pluginName) {
        final XiaomingPlugin plugin = getXiaomingBot().getPluginManager().getPlugin(pluginName);
        if (Objects.isNull(plugin)) {
            user.sendError("没有找到插件：{}", pluginName);
        } else {
            if (user instanceof GroupXiaomingUser) {
                ((GroupXiaomingUser) user).sendPrivateMessage(getPluginMessage(plugin));
            } else {
                user.sendMessage(getPluginMessage(plugin));
            }
        }
    }

    @Command("(维护|调试|debug)")
    @RequirePermission("debug")
    public void onDebug(final XiaomingUser user) {
        final Config config = getXiaomingBot().getConfig();
        config.setDebug(!config.isDebug());
        config.readySave();
        if (config.isDebug()) {
            user.sendMessage("已开启小明的维护状态");
        } else {
            user.sendMessage("已关闭小明的维护状态");
        }
    }

    @Command(CommandWordUtil.COMMAND_EXECUTOR_REGEX)
    @RequirePermission("commandexecutor.list")
    public void onCommandExecutor(final XiaomingUser user) {
        if (user instanceof GroupXiaomingUser) {
            user.sendMessage("指令处理器详情已私发你啦，记得查收");
            ((GroupXiaomingUser) user).sendPrivateMessage(getCommandExecutorsString());
        } else {
            user.sendMessage(getCommandExecutorsString());
        }
    }

    @Command(CommandWordUtil.INTERACTOR_REGEX)
    @RequirePermission("interactor.list")
    public void onInteractorStatus(final XiaomingUser user) {
        if (user instanceof GroupXiaomingUser) {
            user.sendMessage("交互器详情已私发你啦，记得查收");
            ((GroupXiaomingUser) user).sendPrivateMessage(getInteractorString());
        } else {
            user.sendMessage(getInteractorString());
        }
    }

    @Command(CommandWordUtil.CALL_REGEX)
    public void onCallCounter(final XiaomingUser user) {
        user.sendMessage("小明至今的召唤次数：{}", getXiaomingBot().getCounter().getCallCounter());
    }

    @Command(CommandWordUtil.EXCEPTION_REGEX)
    @RequirePermission("debug.exception")
    public void onThrowException(final XiaomingUser user) {
        final XiaomingRuntimeException exception = new XiaomingRuntimeException();
        user.sendMessage("小明将尝试抛出异常：{}", exception.getClass().getSimpleName());
        throw exception;
    }

    @Command(CommandWordUtil.SAVE_REGEX)
    @RequirePermission("save")
    public void onSave(final XiaomingUser user) {
        final RegularSaveDataManager manager = getXiaomingBot().getRegularSaveDataManager();
        if (manager.getSaveSet().isEmpty()) {
            user.sendMessage("当前没有任何文件等待保存哦");
        } else {
            manager.save(user);
        }
    }

    @Command("(指令格式|格式|commandformat|format)")
    public void onGlobalHelp(final XiaomingUser user) {
        List<String> runnableCoreCommands = new ArrayList<>();
        final CommandManager commandManager = getXiaomingBot().getCommandManager();

        for (CommandExecutor coreCommandExecutor : commandManager.getCoreCommandExecutors()) {
            runnableCoreCommands.addAll(coreCommandExecutor.getUsageStrings(user));
        }
        for (Set<CommandExecutor> value : commandManager.getPluginCommandExecutors().values()) {
            for (CommandExecutor commandExecutor : value) {
                runnableCoreCommands.addAll(commandExecutor.getUsageStrings(user));
            }
        }
        StringBuilder builder = new StringBuilder();
        if (runnableCoreCommands.isEmpty()) {
            builder.append("你没有权限执行任何小明指令 {}").append(getXiaomingBot().getEmojiManager().get("sad"));
        } else {
            builder.append("你能可能有权限执行的所有小明指令有 ").append(runnableCoreCommands.size()).append(" 条：");
            Collections.sort(runnableCoreCommands);
            for (String runnableCoreCommand : runnableCoreCommands) {
                builder.append("\n").append(runnableCoreCommand);
            }
        }

        if (user instanceof GroupXiaomingUser) {
            ((GroupXiaomingUser) user).sendPrivateMessage(builder.toString());
        } else {
            user.sendMessage(builder.toString());
        }
    }

    @Command(CommandWordUtil.HELP_REGEX)
    public void onUsage(final XiaomingUser user) {
        user.sendMessage("欢迎使用小明！\n" +
                "你可用的所有小明指令可以通过 #指令格式 查询" + getXiaomingBot().getEmojiManager().get("happy"));

        user.sendMessage("如果你觉得小明很不错，欢迎到 https://github.com/TaixueChina/xiaoming-bot 给我们点亮一颗星星\n" +
                "如果在使用途中小明对你造成了困扰，或者希望邀请小明来你的群，欢迎私聊椽子（QQ：1437100907）或者去上述 Github 提 issue\n" +
                "小明正在学习的技能有：百科词条、和MC服务器互通。期待更好的小明把~\n" +
                "如果你想要编写小明的功能，欢迎打开上述链接查看开发文档。");
    }

    /*
    @Command("(关闭小明|stop)")
    @RequirePermission("stop")
    public void onStop(final XiaomingUser user) {
        final XiaomingLauncher instance = XiaomingLauncher.getInstance();
        instance.getShutdownHook().setUser(user);
        instance.getConsoleCommandRunnable().close();
        System.exit(0);
    }*/
/*
    public void unloadGroupInteactor(CommandSender sender, GroupInteractor interactor) {
        UserDataIsolator userDataIsolator = interactor.getUserDataIsolator();
        if (userDataIsolator.getValue().isEmpty()) {
            sender.sendMessage("\t\t该交互器没有和任何用户交互");
        }
        else {
            sender.sendMessage("\t\t该交互器正在和 {} 名用户交互：", userDataIsolator.getValue().size());
            Set<GroupInteractorUser> userData = (Set<GroupInteractorUser>) (Object) userDataIsolator.getValue().keySet();
            for (GroupInteractorUser userDatum : userData) {
                sender.sendMessage("\t\t\t所在群：{}\n", userDatum.getGroup());
                sender.sendMessage("\t\t\tQQ：{}\n", userDatum.getQQ());
                sender.sendMessage("\t\t\t最后输入：{}\n", userDatum.getMessage());
                try {
                    interactor.onUserOut(userDatum.getQQ());
                }
                catch (Exception exception) {

                }
            }
        }
    }

    public void unloadAllPlugins(CommandSender sender) {
        Map<String, XiaomingPlugin> loadedPlugins = XiaomingBot.getInstance().getPluginManager().getLoadedPlugins();
        for (XiaomingPlugin value : loadedPlugins.values()) {
            try {
                sender.sendMessage("正在卸载插件 {}", value.getName());
                value.onDisable();
                sender.sendMessage("插件 {} 卸载完成", value.getName());
            }
            catch (Exception exception) {
                sender.sendError("卸载 {} 卸载时出现异常：{}", value.getName(), exception);
                exception.printStackTrace();
            }
        }
    }
    */

    /*
    @Command( + " qq {qq}")
    @RequirePermission("console.qq")
    public void onSetConsoleQQ(@NotNull final XiaomingUserImpl user,
                               @CommandParameter("qq") final String qqString) {
        long qq = AtUtil.parseQQ(qqString);
        if (qq == -1) {
            user.sendError("{} 似乎不是一个正确的 QQ 哦", qqString);
        }

        XiaomingLauncher.getInstance().getConsoleXiaomingUser().setQQ(qq);
        user.sendMessage("已设置控制台执行身份为 QQ：{}", qqString);

     */
}