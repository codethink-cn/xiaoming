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
import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.schedule.task.PreservableSaveTask;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.recept.ReceptionistManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.StringUtils;
import com.chuanwise.xiaoming.api.util.TimeUtils;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;
import net.mamoe.mirai.message.code.MiraiCode;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;

public class CoreCommandInteractor extends CommandInteractorImpl {
    public static final String BAT_REGEX = "(批处理|bat)";

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

    /**
     * 批处理指令
     * @param user 指令执行者
     * @param remain 指令
     */
    @Filter("#" + BAT_REGEX + "#" + "{remain}")
    public void onMultipleCommands(XiaomingUser user,
                                   @FilterParameter("remain") String remain,
                                   Message message) {
        final String[] subCommands = remain.split(Pattern.quote("\\n"), 0);

        user.enableBuffer();
        int commandNumber = 0;
        try {
            for (int i = 1; i < subCommands.length; i++) {
                String command = subCommands[i];
                if (command.isEmpty()) {
                    continue;
                }
                final Message clonedMessage = message.clone();
                clonedMessage.setMessageChain(MiraiCode.deserializeMiraiCode(subCommands[i]));

                if (getXiaomingBot().getInteractorManager().onInput(user, clonedMessage)) {
                    commandNumber++;
                } else {
                    user.setProperty("command", command);
                    user.sendError("{illegalCommandInterruptBatchTask}");
                    break;
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

    @Filter(CommandWords.SAVE)
    @Require("core.save")
    public void onSave(XiaomingUser user) {
        final PreservableSaveTask fileSaver = getXiaomingBot().getScheduler().getPreservableSaveTask();
        if (fileSaver.getPreservables().isEmpty()) {
            user.sendMessage("{noFileNeedToSave}");
        } else {
            fileSaver.save(user);
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
            for (Interactor interactor : coreInteractors) {
                if (!(interactor instanceof CommandInteractor)) {
                    continue;
                }
                final List<String> usageStrings = Arrays.asList(interactor.getUsageStrings(user).toArray(new String[0]));
                Collections.sort(usageStrings);
                if (usageStrings.isEmpty()) {
                    printWriter.println(interactor.getName() + "：（无）");
                } else {
                    printWriter.println(interactor.getName() + "：（" + usageStrings.size() + " 种）");
                    printWriter.println(StringUtils.getCollectionSummary(usageStrings));
                }
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
                    printWriter.println(plugin.getName() + "：");
                    for (Interactor interactor : interactors) {
                        if (!(interactor instanceof CommandInteractor)) {
                            continue;
                        }
                        final List<String> usageStrings = Arrays.asList(interactor.getUsageStrings(user).toArray(new String[0]));
                        Collections.sort(usageStrings);
                        if (usageStrings.isEmpty()) {
                            printWriter.println(interactor.getName() + "：（无）");
                        } else {
                            printWriter.println(interactor.getName() + "：（" + usageStrings.size() + " 种）");
                            printWriter.println(StringUtils.getCollectionSummary(usageStrings));
                        }
                    }
                }
            }
        }

        user.sendMessage(stringWriter.toString().trim());
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
            final long delay = TimeUnit.SECONDS.toMicros(10);
            final String delayString = TimeUtils.toTimeString(delay);

            getXiaomingBot().getScheduler().runLater(() -> {
                getXiaomingBot().stop();
            }, delay);

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
                    StringUtils.getCollectionSummary(receptionists, receptionist -> {
                        return receptionist.getCode() + "：" + (receptionist.isBusy() ? "忙碌" : "空闲");
                    }));
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

            Function<Map.Entry<String, ? extends ReceptionTask>, String> groupOrTempTaskStatusFormatter = entry -> {
                return entry.getKey() + "：" + (entry.getValue().isBusy() ? "忙碌" : "空闲");
            };
            builder.append("群接待任务：")
                    .append(StringUtils.getCollectionSummary(receptionist.getGroupTasks().entrySet(), groupOrTempTaskStatusFormatter::apply))
                    .append("\n")
                    .append("临时会话接待任务：")
                    .append(StringUtils.getCollectionSummary(receptionist.getTempTasks().entrySet(), groupOrTempTaskStatusFormatter::apply));
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
            user.sendMessage("{receptionistClosedSuccessfully}");
        }
    }

    @Filter(CommandWords.XIAOMING + CommandWords.STATUS)
    @Require("core.status")
    public void onStatus(XiaomingUser user) {
        final long lastSaveTime = getXiaomingBot().getScheduler().getPreservableSaveTask().getLastSaveTime();
        StringBuilder builder = new StringBuilder();

        final Statistician statistician = getXiaomingBot().getStatistician();

        builder.append("{xiaoming}启动于：").append(TimeUtils.FORMAT.format(statistician.getBeginTime())).append("\n")
                .append("已运行：").append(TimeUtils.toTimeString(System.currentTimeMillis() - statistician.getBeginTime())).append("\n")
                .append("上次文件保存时间：").append(TimeUtils.FORMAT.format(lastSaveTime)).append("\n")
                .append("内存空置率：").append(String.format("%.2f%%", (double) 100 * Runtime.getRuntime().freeMemory() / Runtime.getRuntime().maxMemory())).append("\n")
                .append("距今：").append(TimeUtils.toTimeString(System.currentTimeMillis() - lastSaveTime));

        user.sendMessage(builder.toString());
    }

    @Override
    public <T> Object onParameter(XiaomingUser user, Class<T> clazz, String parameterName, String currentValue, String defaultValue) {
        Object parameter = super.onParameter(user, clazz, parameterName, currentValue, defaultValue);
        if (long.class.isAssignableFrom(clazz) && Objects.equals(parameterName, "group")) {
            if (currentValue.matches("\\d+")) {
                return Long.parseLong(currentValue);
            } else {
                user.sendError("{}并不是一个合理的群号哦", currentValue);
            }
        }
        return parameter;
    }
}