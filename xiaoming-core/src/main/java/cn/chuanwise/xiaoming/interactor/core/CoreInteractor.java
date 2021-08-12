package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.utility.StringUtility;
import cn.chuanwise.utility.TimeUtility;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.configuration.Statistician;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.interactor.InteractorManager;
import cn.chuanwise.xiaoming.interactor.Interactor;
import cn.chuanwise.xiaoming.plugin.XiaomingPlugin;
import cn.chuanwise.xiaoming.schedule.FileSaver;
import cn.chuanwise.xiaoming.recept.Receptionist;
import cn.chuanwise.xiaoming.recept.ReceptionistManager;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.utility.CommandWords;
import cn.chuanwise.xiaoming.interactor.InteractorImpl;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class CoreInteractor extends InteractorImpl {
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

    public CoreInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
    }

    @Filter(CommandWords.INTERACTOR)
    @Permission("core.interactor.list")
    public void onInteractorStatus(XiaomingUser user) {
        user.sendMessage(getInteractorString());
    }

    @Filter(CommandWords.CALL)
    @Permission("core.statistics.call")
    public void onCallCounter(XiaomingUser user) {
        user.sendMessage("{lang.callNumber}");
    }

    /** 批处理指令 */
    @Filter("#" + BATCH + "#" + "{remain}")
    public void onMultipleCommands(XiaomingUser user,
                                   @FilterParameter("remain") String remain,
                                   Message message) {
        final String[] subCommands = remain.split(Pattern.quote("\\n"), 0);
        final long maxJoinTime = TimeUnit.SECONDS.toMillis(1);

        user.enablePrintWriter();
        try {
            Future<Boolean> task = null;
            for (int i = 1; i < subCommands.length; i++) {
                String command = subCommands[i];
                if (command.isEmpty()) {
                    continue;
                }

                if (Objects.isNull(task) || task.isDone()) {
                    int finalI = i;
                    task = getXiaomingBot().getScheduler().run(() -> {
                        final Message clonedMessage = message.clone();
                        final MessageChain messageChain = MiraiCode.deserializeMiraiCode(subCommands[finalI]);
                        clonedMessage.setMessageChain(messageChain);
                        clonedMessage.setOriginalMessageChain(messageChain);

                        return getXiaomingBot().getInteractorManager().onInput(user, clonedMessage);
                    });
                    if (!task.get()) {
                        user.sendError("{lang.batchTaskInterruptedByIllegalCommand}", command);
                        break;
                    }
                } else {
                    user.onNextInput(command);
                }
            }
            if (Objects.nonNull(task) && !task.isDone()) {
                task.get();
            }
        } catch (Exception exception) {
            user.sendError("{lang.batchTaskInterruptedByException}", exception);
            exception.printStackTrace();
        }
        final String buffer = user.getBufferAndClose();
        user.sendMessage(StringUtility.isEmpty(buffer) ? "{lang.batchTaskNoAnyOutput}" : buffer);
    }

    @Filter(CommandWords.OPTIMIZE)
    public void onOptimize(XiaomingUser user) {
        getXiaomingBot().getOptimizer().optimize();
        user.sendMessage("{lang.optimized}");
    }

    @Filter(CommandWords.SAVE)
    @Permission("core.save.do")
    public void onSave(XiaomingUser user) {
        final FileSaver fileSaver = getXiaomingBot().getFileSaver();
        final Map<File, Preservable<File>> preservables = fileSaver.getPreservables();
        final int sizeBeforeSave = preservables.size();

        if (sizeBeforeSave == 0) {
            user.sendMessage("{lang.noFileNeedToSave}");
        } else {
            fileSaver.save();
            if (preservables.isEmpty()) {
                user.sendMessage("{lang.fileSaved}", sizeBeforeSave);
            } else if (sizeBeforeSave == preservables.size()) {
                user.sendError("{lang.noFileSaved}", preservables.values());
            } else {
                user.sendError("{lang.noFileSaved}", sizeBeforeSave - preservables.size(), preservables.size(), preservables.values());
            }
        }
    }

    @Filter(CommandWords.CANCEL + CommandWords.SAVE)
    @Permission("core.save.cancel")
    public void onCancelSave(XiaomingUser user) {
        final FileSaver task = getXiaomingBot().getFileSaver();
        final Map<File, Preservable<File>> preservables = task.getPreservables();

        if (preservables.isEmpty()) {
            user.sendMessage("{lang.noFileNeedToSave}");
        } else {
            user.sendWarning("{lang.confirmCancelSaveFile}");
            if (Objects.equals(user.nextInput().serialize(), "确定")) {
                preservables.clear();
                user.sendMessage("{lang.savePlanCancelled}");
            } else {
                user.sendMessage("{lang.savePlanNoCancelled}");
            }
        }
    }

    @Filter(CommandWords.ECHO + " {remain}")
    @Permission("core.echo")
    public void onEcho(XiaomingUser user, @FilterParameter("remain") String remain) {
        if (StringUtility.nonEmpty(remain)) {
            user.sendMessage(remain);
        }
    }

    @Filter(CommandWords.COMMAND + CommandWords.FORMAT)
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
            List<String> coreCommands = new LinkedList<>();
            for (Interactor interactor : coreInteractors) {
                coreCommands.addAll(Arrays.asList(interactor.getUsageStrings(user).toArray(new String[0])));
            }

            Collections.sort(coreCommands);
            for (String command : coreCommands) {
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

                if (!user.isBlockPlugin(plugin)) {
                    printWriter.println("\n" + plugin.getAlias() + "：");

                    List<String> pluginCommands = new LinkedList<>();
                    for (Interactor interactor : interactors) {
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
    @Permission("core.help")
    public void onGlobalHelp(XiaomingUser user) {
        user.sendMessage("{lang.welcomeToUseXiaoming}");
    }

    @Filter(CommandWords.DISABLE + CommandWords.XIAOMING)
    @Permission("core.stop")
    public void onCloseXiaoming(XiaomingUser user) {
        user.sendMessage("{lang.confirmCloseXiaoming}");

        if (Objects.equals(user.nextInput(TimeUnit.MINUTES.toMillis(1)).serialize(), "确定")) {
            final long delay = TimeUnit.SECONDS.toMillis(10);
            getXiaomingBot().getScheduler().runLater(delay, () -> {
                getXiaomingBot().stop();
            });

            user.sendMessage("{lang.xiaomingWillBeCousedLater}", delay);
        } else {
            user.sendError("{lang.xiaomingWillNotBeCousedLater}");
        }
    }

    @Filter(CommandWords.STOP)
    public void onConsoleClose(ConsoleXiaomingUser user) {
        getXiaomingBot().stop();
    }

    @Filter(CommandWords.RECEPTIONIST)
    @Permission("core.receptionist.list")
    public void onReceptionist(XiaomingUser user) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();
        final Collection<Receptionist> receptionists = receptionistManager.getReceptionists().values();

        if (receptionists.isEmpty()) {
            user.sendMessage("{lang.noAnyReceptionist}");
        } else {
            user.sendMessage("{lang.receptionistList}");
        }
    }

    @Filter(CommandWords.RECEPTIONIST + " {qq}")
    @Permission("core.receptionist.look")
    public void onReceptionist(XiaomingUser user, @FilterParameter("qq") long qq) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();
        final Receptionist receptionist = receptionistManager.forReceptionist(qq);

        if (Objects.isNull(receptionist)) {
            user.sendMessage("{lang.userHasNotReceptionist}");
        } else {
            user.sendMessage("{lang.userReceptionistDetail}", receptionist);
        }
    }

    @Filter(CommandWords.DISABLE + CommandWords.RECEPTIONIST + " {qq}")
    @Permission("core.receptionist.disable")
    public void onDisableReceptionist(XiaomingUser user, @FilterParameter("qq") long qq) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();
        final Receptionist receptionist = receptionistManager.forReceptionist(qq);

        if (Objects.isNull(receptionist)) {
            user.sendMessage("{lang.userHasNotReceptionist}");
        } else {
            if (receptionist.isBusy()) {
                receptionist.optimize();
                user.sendMessage("{lang.optimizedReceptionist}", qq);
            } else {
                receptionist.stop();
                user.sendMessage("{lang.receptionistClosed}");
            }
        }
    }

    @Filter(CommandWords.OPTIMIZE + CommandWords.RECEPTIONIST + " {qq}")
    @Permission("core.receptionist.optimize")
    public void onOptimizeReceptionist(XiaomingUser user, @FilterParameter("qq") long qq) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();
        final Receptionist receptionist = receptionistManager.forReceptionist(qq);

        if (Objects.isNull(receptionist)) {
            user.sendMessage("{lang.noAnyReceptionist}");
        } else {
            receptionist.optimize();
            user.sendMessage("{lang.receptionistOptimized}");
        }
    }

    @Filter(CommandWords.OPTIMIZE + CommandWords.RECEPTIONIST)
    @Permission("core.receptionist.optimize")
    public void onOptimizeReceptionist(XiaomingUser user) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();

        receptionistManager.optimize();
        user.sendMessage("{lang.receptionistOptimized}");
    }

    @Filter(CommandWords.FORCE + CommandWords.DISABLE + CommandWords.RECEPTIONIST + " {qq}")
    @Permission("core.receptionist.disable")
    public void onForceDisableReceptionist(XiaomingUser user, @FilterParameter("qq") long qq) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();
        final Receptionist receptionist = receptionistManager.forReceptionist(qq);

        if (Objects.isNull(receptionist)) {
            user.sendMessage("{lang.userHasNotReceptionist}");
        } else {
            receptionist.stop();
            receptionistManager.getReceptionists().remove(qq);
            user.sendMessage("{lang.receptionistClosed}");
        }
    }

    @Filter(CommandWords.XIAOMING + CommandWords.STATUS)
    @Permission("core.status")
    public void onStatus(XiaomingUser user) {
        user.sendMessage( "{lang.xiaomingStatus}");
    }
}