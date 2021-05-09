package com.chuanwise.xiaoming.core.command.executor;

import com.chuanwise.xiaoming.api.annotation.Command;
import com.chuanwise.xiaoming.api.annotation.CommandParameter;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.command.executor.CommandExecutor;
import com.chuanwise.xiaoming.api.command.executor.CommandManager;
import com.chuanwise.xiaoming.api.config.Configuration;
import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.plugin.PluginManager;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.runnable.RegularPreserveManager;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.TimeUtil;

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

        final PluginManager pluginManager = getXiaomingBot().getPluginManager();
        final Set<XiaomingPlugin> loadedPlugins = pluginManager.getLoadedPlugins();
        builder.append("加载的插件：");
        if (loadedPlugins.isEmpty()) {
            builder.append("（无）");
        } else {
            for (XiaomingPlugin value : loadedPlugins) {
                builder.append("\n").append(value.getCompleteName());
            }
        }
        builder.append("\n");

        final Set<XiaomingPlugin> enabledPlugins = pluginManager.getEnabledPlugins();
        builder.append("启动的插件：");
        if (enabledPlugins.isEmpty()) {
            builder.append("（无）");
        } else {
            for (XiaomingPlugin value : enabledPlugins) {
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

    public CoreCommandExecutor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
    }

    @Command(CommandWords.PLUGIN_REGEX)
    @RequirePermission("plugin.list")
    public void onLoadedPlugins(XiaomingUser user) {
        user.sendMessage(getLoadedPluginString());
    }

    @Command(CommandWords.PLUGIN_REGEX + " {plugin}")
    @RequirePermission("plugin.look")
    public void onLoadedPlugins(XiaomingUser user,
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
    public void onDebug(XiaomingUser user) {
        final Configuration config = getXiaomingBot().getConfig();
        config.setDebug(!config.isDebug());
        getXiaomingBot().getRegularPreserveManager().readySave(config);
        if (config.isDebug()) {
            user.sendMessage("已开启小明的维护状态");
        } else {
            user.sendMessage("已关闭小明的维护状态");
        }
    }

    @Command(CommandWords.COMMAND_EXECUTOR_REGEX)
    @RequirePermission("commandexecutor.list")
    public void onCommandExecutor(XiaomingUser user) {
        if (user instanceof GroupXiaomingUser) {
            user.sendMessage("指令处理器详情已私发你啦，记得查收");
            ((GroupXiaomingUser) user).sendPrivateMessage(getCommandExecutorsString());
        } else {
            user.sendMessage(getCommandExecutorsString());
        }
    }

    @Command(CommandWords.INTERACTOR_REGEX)
    @RequirePermission("interactor.list")
    public void onInteractorStatus(XiaomingUser user) {
        if (user instanceof GroupXiaomingUser) {
            user.sendMessage("交互器详情已私发你啦，记得查收");
            ((GroupXiaomingUser) user).sendPrivateMessage(getInteractorString());
        } else {
            user.sendMessage(getInteractorString());
        }
    }

    @Command(CommandWords.CALL_REGEX)
    public void onCallCounter(XiaomingUser user) {
        user.sendMessage("小明至今的召唤次数：{}", getXiaomingBot().getStatistician().getCallNumber());
    }

    @Command(CommandWords.EXCEPTION_REGEX)
    @RequirePermission("debug.exception")
    public void onThrowException(XiaomingUser user) {
        final XiaomingRuntimeException exception = new XiaomingRuntimeException();
        user.sendMessage("小明将尝试抛出异常：{}", exception.getClass().getSimpleName());
        throw exception;
    }

    @Command(CommandWords.SAVE_REGEX)
    @RequirePermission("save")
    public void onSave(XiaomingUser user) {
        final RegularPreserveManager manager = getXiaomingBot().getRegularPreserveManager();
        if (manager.getPreservables().isEmpty()) {
            user.sendMessage("当前没有任何文件等待保存哦");
        } else {
            manager.save(user);
        }
    }

    @Command("(指令格式|格式|usage|format)")
    public void onGlobalUsage(XiaomingUser user) {
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
            builder.append("你没有权限执行任何小明指令 {}").append(getXiaomingBot().getWordManager().get("sad"));
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

    @Command(CommandWords.HELP_REGEX)
    public void onGlobalHelp(XiaomingUser user) {
        user.sendMessage("欢迎使用小明！\n" +
                "你可用的所有小明指令可以通过 #指令格式 查询" + getXiaomingBot().getWordManager().get("happy"));

        user.sendMessage("如果你觉得小明很不错，欢迎到 https://github.com/TaixueChina/xiaoming-bot 给我们点亮一颗星星\n" +
                "如果在使用途中小明对你造成了困扰，或者希望邀请小明来你的群，欢迎私聊椽子（QQ：1437100907）或者去上述 Github 提 issue\n" +
                "小明正在学习的技能有：百科词条、和MC服务器互通。期待更好的小明把~\n" +
                "如果你想要编写小明的功能，欢迎打开上述链接查看开发文档。");
    }

    long lastCloseConfirmTime;

    @Command(CommandWords.DISABLE_REGEX + CommandWords.XIAOMING_REGEX)
    @RequirePermission("stop")
    public void onCloseXiaoming(XiaomingUser user) {
        user.sendMessage("你确定要关闭小明吗？如果是，请在一分钟之内发送「#确定关闭小明」");
        lastCloseConfirmTime = System.currentTimeMillis() + TimeUtil.MINUTE_MINS;
    }

    @Command(CommandWords.CONFIRM_REGEX + CommandWords.DISABLE_REGEX + CommandWords.XIAOMING_REGEX)
    @RequirePermission("stop")
    public void onConfirmCloseXiaoming(XiaomingUser user) {
        if (lastCloseConfirmTime > System.currentTimeMillis()) {
            getXiaomingBot().stop(user);
        } else {
            user.sendError("没有需要确认的小明关闭操作");
        }
    }

    /*
    @Command("(关闭小明|stop)")
    @RequirePermission("stop")
    public void onStop(XiaomingUser user) {
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