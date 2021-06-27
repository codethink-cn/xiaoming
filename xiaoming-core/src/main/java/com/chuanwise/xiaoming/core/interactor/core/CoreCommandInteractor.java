package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.Require;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.configuration.Statistician;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.interactor.InteractorManager;
import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.interactor.command.CommandInteractor;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.schedule.Scheduler;
import com.chuanwise.xiaoming.api.schedule.task.PreservableSaveTask;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.recept.ReceptionistManager;
import com.chuanwise.xiaoming.api.schedule.task.ScheduableTask;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CollectionUtils;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.TimeUtils;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;
import com.chuanwise.xiaoming.core.schedule.SchedulerImpl;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;

public class CoreCommandInteractor extends CommandInteractorImpl {
    public static final String BATCH = "(批处理|bat)";

    public String getInteractorString() {
        final InteractorManager interactorManager = getXiaomingBot().getInteractorManager();
        StringBuilder builder = new StringBuilder("交互器：\n");

        builder.append("内核注册：");
        final Set<Interactor> coreInteractors = interactorManager.getCoreInteractors();
        if (coreInteractors.isEmpty()) {
            builder.append("（无）");
        } else {
            for (Interactor interactor : coreInteractors) {
                builder.append("\n")
                        .append("# ").append(interactor.getClass().getName());
            }
        }
        builder.append("\n");

        builder.append("插件注册：");
        final Map<XiaomingPlugin, Set<Interactor>> pluginInteractors = interactorManager.getPluginInteractors();
        if (pluginInteractors.isEmpty()) {
            builder.append("（无）");
        } else {
            for (Map.Entry<XiaomingPlugin, Set<Interactor>> entry : pluginInteractors.entrySet()) {
                builder.append("\n")
                        .append("> 由").append(entry.getKey().getCompleteName()).append("注册：");
                for (Interactor interactor : entry.getValue()) {
                    builder.append("\n")
                            .append(">> ").append(interactor.getClass().getName());
                }
            }
        }
        return builder.toString();
    }

    public CoreCommandInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
    }

    @Filter(CommandWords.INTERACTOR)
    @Require("core.interactor.list")
    public void onInteractorStatus(XiaomingUser user) {
        user.sendMessage(getInteractorString());
    }

    @Filter(CommandWords.CALL)
    @Require("core.statistics.call")
    public void onCallCounter(XiaomingUser user) {
        user.setProperty("counter", getXiaomingBot().getStatistician().getCallNumber());
        user.sendMessage("{callCounterIs}");
    }

    @Filter(CommandWords.TASK + "(队列|queue)")
    @Require("core.scheduler.list")
    public void onListScheduler(XiaomingUser user) {
        final Set<ScheduableTask<?>> runningTasks = getXiaomingBot().getScheduler().getRunningTasks();
        final Set<ScheduableTask<?>> plannedTasks = getXiaomingBot().getScheduler().getPlannedTasks();

        StringBuilder builder = new StringBuilder();
        builder.append("执行中：").append(CollectionUtils.getSummary(runningTasks, ScheduableTask::getDescription, "\n")).append("\n")
                .append("计划中：").append(CollectionUtils.getSummary(plannedTasks, task -> {
            return task.getDescription() + "：" + (task.isTimeout() ? "已过期" : TimeUtils.after(task.getDelay()));
        }, "\n"));
        user.sendMessage(builder.toString());
    }

    /**
     * 批处理指令
     * @param user 指令执行者
     * @param remain 指令
     */
    @Filter("#" + BATCH + "#" + "{remain}")
    public void onMultipleCommands(XiaomingUser user,
                                   @FilterParameter("remain") String remain,
                                   Message message) {
        final String[] subCommands = remain.split(Pattern.quote("\\n"), 0);

        user.enablePrintWriter();
        int commandNumber = 0;
        try {
            for (int i = 1; i < subCommands.length; i++) {
                String command = subCommands[i];
                if (command.isEmpty()) {
                    continue;
                }
                final Message clonedMessage = message.clone();
                final MessageChain messageChain = MiraiCode.deserializeMiraiCode(subCommands[i]);
                clonedMessage.setMessageChain(messageChain);
                clonedMessage.setOriginalMessageChain(messageChain);

                ScheduableTask<Boolean> task;
                if (Objects.isNull(user.getInteractor())) {
                    task = getXiaomingBot().getScheduler().run(() -> {
                        return getXiaomingBot().getInteractorManager().onInput(user, clonedMessage);
                    });
                    task.setDescription("批处理临时接待任务");
                    if (Objects.isNull(user.getInteractor())) {
                        try {
                            if (!task.get()) {
                                user.setProperty("command", command);
                                user.sendError("{illegalCommandInterruptBatchTask}");
                                break;
                            } else {
                                commandNumber++;
                            }
                        } catch (InterruptedException ignored) {
                        }
                    }
                } else {
                    user.onNextInput(clonedMessage);
                    commandNumber++;
                }
            }
        } catch (Exception exception) {
            user.sendError("{exceptionInterruptBatchTask}");
            exception.printStackTrace();
        }

        final String bufferString = user.getBufferAndClose();
        user.sendMessage(bufferString);

        if (commandNumber == 0) {
            user.sendError("{noCommandExecuted}");
        } else {
            user.setProperty("counter", commandNumber);
            user.sendMessage("{executeCommandSuccessfully}");
        }
    }

    @Filter(CommandWords.OPTIMIZE)
    public void onOptimize(XiaomingUser user) {
        getXiaomingBot().getScheduler().runLater(TimeUnit.SECONDS.toMillis(10), getXiaomingBot()::optimize).setDescription("手动优化任务");
        user.sendMessage("{optimizedSuccessfully}");
    }

    @Filter(CommandWords.SAVE)
    @Require("core.save.do")
    public void onSave(XiaomingUser user) {
        final PreservableSaveTask fileSaver = getXiaomingBot().getScheduler().getPreservableSaveTask();
        if (fileSaver.getPreservables().isEmpty()) {
            user.sendMessage("{noFileNeedToSave}");
        } else {
            fileSaver.save(user);
        }
    }

    @Filter(CommandWords.CANCEL + CommandWords.SAVE)
    @Require("core.save.cancel")
    public void onCancelSave(XiaomingUser user) {
        final PreservableSaveTask task = getXiaomingBot().getScheduler().getPreservableSaveTask();
        final Set<Preservable<?>> preservables = task.getPreservables();

        if (preservables.isEmpty()) {
            user.sendMessage("没有任何等待保存的文件");
        } else {
            user.sendWarning("真的要取消保存 " + preservables.size() + " 个文件吗？这可能导致部分数据丢失！如果确定，请在一分钟之内回复「确定」");
            if (Objects.equals(user.nextInput().serialize(), "确定")) {
                preservables.clear();
                user.sendMessage("成功取消保存待保存的文件队列");
            } else {
                user.sendMessage("已取消取消保存");
            }
        }
    }

    @Filter("(指令格式|格式|usage|format)")
    public void onGlobalUsage(XiaomingUser user) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        final InteractorManager interactorManager = getXiaomingBot().getInteractorManager();

        // 获取内核帮助
        final Set<Interactor> coreInteractors = interactorManager.getCoreInteractors();
        if (coreInteractors.isEmpty()) {
            printWriter.println("内核指令：（无）");
        } else {
            printWriter.println("内核指令：");
            List<String> kernalCommands = new LinkedList<>();
            for (Interactor interactor : coreInteractors) {
                if (!(interactor instanceof CommandInteractor)) {
                    continue;
                }
                kernalCommands.addAll(Arrays.asList(interactor.getUsageStrings(user).toArray(new String[0])));
            }

            Collections.sort(kernalCommands);
            for (String command : kernalCommands) {
                printWriter.println(command);
            }
        }

        // 获取插件帮助
        printWriter.println();
        final Map<XiaomingPlugin, Set<Interactor>> pluginInteractors = interactorManager.getPluginInteractors();
        if (pluginInteractors.isEmpty()) {
            printWriter.println("插件指令：（无）");
        } else {
            printWriter.println("插件指令：");

            for (Map.Entry<XiaomingPlugin, Set<Interactor>> entry : pluginInteractors.entrySet()) {
                final XiaomingPlugin plugin = entry.getKey();
                final Set<Interactor> interactors = entry.getValue();

                if (!user.isBlockPlugin(plugin.getName())) {
                    printWriter.println("\n" + plugin.getAlias() + "：");

                    List<String> pluginCommands = new LinkedList<>();
                    for (Interactor interactor : interactors) {
                        if (!(interactor instanceof CommandInteractor)) {
                            continue;
                        }
                        pluginCommands.addAll(Arrays.asList(interactor.getUsageStrings(user).toArray(new String[0])));
                    }

                    Collections.sort(pluginCommands);
                    for (String pluginCommand : pluginCommands) {
                        printWriter.println(pluginCommand);
                    }
                }
            }
        }

        user.sendPrivateMessage(stringWriter.toString().trim());
    }

    @Filter(CommandWords.HELP)
    @Require("core.help")
    public void onGlobalHelp(XiaomingUser user) {
        user.sendMessage("欢迎使用小明！\n" +
                "你可用的所有小明指令可以通过「指令格式」查询 {happy}");

        user.sendMessage("如果你觉得小明很不错，欢迎到 https://github.com/TaixueChina/xiaoming-bot 给我们点亮一颗星星\n" +
                "如果在使用途中小明对你造成了困扰，或者希望邀请小明来你的群，欢迎私聊椽子（QQ：1437100907）或者去上述 Github 提 issue\n" +
                "小明正在学习的技能有：百科词条、和MC服务器互通。期待更好的小明把~\n" +
                "如果你想要编写小明的功能，欢迎打开上述链接查看开发文档。");
    }

    @Filter(CommandWords.DISABLE + CommandWords.XIAOMING)
    @Require("core.stop")
    public void onCloseXiaoming(XiaomingUser user) {
        user.sendMessage("{doYouReallyWantToCloseXiaoming}");
        Runnable onCancel = () -> {
            user.sendError("{closeXiaomingPlanWasCancelled}");
        };

        if (Objects.equals(user.nextInput(TimeUnit.MINUTES.toMillis(1), onCancel).serialize(), "确定")) {
            final long delay = TimeUnit.SECONDS.toMillis(10);
            final String delayString = TimeUtils.toTimeString(delay);

            getXiaomingBot().getScheduler().runLater(delay, () -> {
                getXiaomingBot().stop();
            });

            user.setProperty("delay", delayString);
            user.sendMessage("{xiaomingWillBeCloseLater}");
        } else {
            onCancel.run();
        }
    }

    @Filter(CommandWords.RECEPTION)
    @Require("core.receptionist.list")
    public void onReceptionist(XiaomingUser user) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();
        final Collection<Receptionist> receptionists = receptionistManager.getReceptionists().values();

        if (receptionists.isEmpty()) {
            user.sendMessage("{thereIsNoAnyReceptionist}");
        } else {
            user.sendMessage("当前共有 {} 个{receptionist}：{}",
                    receptionists.size(),
                    CollectionUtils.getSummary(receptionists, receptionist -> {
                        return receptionist.getCode() + "：" + (receptionist.isBusy() ? "忙碌" : "空闲");
                    }, "\n"));
        }
    }

    @Filter(CommandWords.RECEPTION + " {qq}")
    @Require("core.receptionist.look")
    public void onReceptionist(XiaomingUser user, @FilterParameter("qq") long qq) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();
        final Receptionist receptionist = receptionistManager.getReceptionist(qq);

        if (Objects.isNull(receptionist)) {
            user.sendMessage("{thisUserHasNotReceptionist}");
        } else {
            final StringBuilder builder = new StringBuilder();

            Function<ReceptionTask, String> singleTaskStatusFormatter = task -> {
                if (Objects.isNull(task)) {
                    return "（无）";
                } else if (task.isBusy()) {
                    return "忙碌";
                } else {
                    return "空闲";
                }
            };

            builder.append("私聊接待任务：")
                    .append(singleTaskStatusFormatter.apply(receptionist.getPrivateTask()))
                    .append("\n");

            Function<Map.Entry<String, ? extends ReceptionTask>, String> groupOrMemberTaskStatusFormatter = entry -> {
                return entry.getKey() + "：" + (entry.getValue().isBusy() ? "忙碌" : "空闲");
            };
            builder.append("群接待任务：")
                    .append(CollectionUtils.getSummary(receptionist.getGroupTasks().entrySet(), groupOrMemberTaskStatusFormatter::apply))
                    .append("\n")
                    .append("临时会话接待任务：")
                    .append(CollectionUtils.getSummary(receptionist.getMemberTasks().entrySet(), groupOrMemberTaskStatusFormatter::apply));
            user.sendMessage(builder.toString());
        }
    }

    @Filter(CommandWords.DISABLE + CommandWords.RECEPTION + " {qq}")
    @Require("core.receptionist.disable")
    public void onDisableReceptionist(XiaomingUser user, @FilterParameter("qq") long qq) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();
        final Receptionist receptionist = receptionistManager.getReceptionist(qq);

        if (Objects.isNull(receptionist)) {
            user.sendMessage("{thisUserHasNotReceptionist}");
        } else {
            if (receptionist.isBusy()) {
                receptionist.optimize();
                user.sendMessage("{tryOptimizedReceptionist}");
            } else {
                receptionist.stop();
                user.sendMessage("{receptionistClosedSuccessfully}");
            }
        }
    }

    @Filter(CommandWords.OPTIMIZE + CommandWords.RECEPTION + " {qq}")
    @Require("core.receptionist.optimize")
    public void onOptimizeReceptionist(XiaomingUser user, @FilterParameter("qq") long qq) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();
        final Receptionist receptionist = receptionistManager.getReceptionist(qq);

        if (Objects.isNull(receptionist)) {
            user.sendMessage("{thereIsNoAnyReceptionist}");
        } else {
            receptionist.optimize();
            user.sendMessage("{optimizedReceptionistSuccessfully}");
        }
    }

    @Filter(CommandWords.OPTIMIZE + CommandWords.RECEPTION)
    @Require("core.receptionist.optimize")
    public void onOptimizeReceptionist(XiaomingUser user) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();

        receptionistManager.optimize();
        user.sendMessage("{optimizedAllReceptionistSuccessfully}");
    }

    @Filter("(强制|force)" + CommandWords.DISABLE + CommandWords.RECEPTION + " {qq}")
    @Require("core.receptionist.disable")
    public void onForceDisableReceptionist(XiaomingUser user, @FilterParameter("qq") long qq) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();
        final Receptionist receptionist = receptionistManager.getReceptionist(qq);

        if (Objects.isNull(receptionist)) {
            user.sendMessage("{thereIsNoAnyReceptionist}");
        } else {
            receptionist.stop();
            receptionistManager.getReceptionists().remove(qq);
            user.sendMessage("{receptionistClosedSuccessfully}");
        }
    }

    @Filter(CommandWords.XIAOMING + CommandWords.STATUS)
    @Require("core.status")
    public void onStatus(XiaomingUser user) {
        final Scheduler scheduler = getXiaomingBot().getScheduler();
        final long lastSaveTime = scheduler.getPreservableSaveTask().getLastSaveTime();
        StringBuilder builder = new StringBuilder();
        final Statistician statistician = getXiaomingBot().getStatistician();

        user.sendMessage( "{xiaoming}启动于：" + TimeUtils.FORMAT.format(statistician.getBeginTime()) + "\n" +
                 "已运行：" + TimeUtils.toTimeString(System.currentTimeMillis() - statistician.getBeginTime()) + "\n" +
                 "上次文件保存时间：" + TimeUtils.FORMAT.format(lastSaveTime) + "\n" +
                 "距今：" + TimeUtils.toTimeString(System.currentTimeMillis() - lastSaveTime) + "\n" +
                 "内核版本：" + XiaomingBot.VERSION + "\n" +
                 "待处理任务数：" + scheduler.getPlannedTasks().size() + "\n" +
                 "正在执行的任务数：" + scheduler.getRunningTasks().size() + "\n" +
                 "待保存文件数：" + scheduler.getPreservableSaveTask().getPreservables().size() + "\n" +
                 "内存使用率：" + String.format("%.2f%%", 100 - ((double) 100 * Runtime.getRuntime().freeMemory() / Runtime.getRuntime().maxMemory())));
    }
}