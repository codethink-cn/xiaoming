package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.MapUtility;
import cn.chuanwise.utility.StringUtility;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.interactor.InteractorManager;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.interactor.handler.InteractorHandler;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.schedule.FileSaver;
import cn.chuanwise.xiaoming.recept.Receptionist;
import cn.chuanwise.xiaoming.recept.ReceptionistManager;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.utility.CommandWords;
import cn.chuanwise.xiaoming.utility.InteractorUtility;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CoreInteractors extends SimpleInteractors {
    public static final String BATCH = "(批处理|bat)";

    private void collectRegisters(Plugin plugin, Object object, Map<String, List<String>> results) {
        final String pluginName = Objects.isNull(plugin) ? "小明内核" : plugin.getName();
        MapUtility.getOrPutSupply(results, pluginName, ArrayList::new).add(object.getClass().getSimpleName());
    }

    @Filter(CommandWords.INTERACTOR)
    @Permission("core.interactor.list")
    public void onInteractor(XiaomingUser user) {
        final Map<String, List<String>> interactorDetails = new HashMap<>();

        final InteractorManager interactorManager = getXiaomingBot().getInteractorManager();
        interactorManager.getInteractors().forEach(interactor -> collectRegisters(interactor.getPlugin(), interactor.getInteractors(), interactorDetails));

        user.sendMessage("{lang.interactors}", interactorDetails);
    }

    @Filter(CommandWords.CALL)
    @Permission("core.statistics.call")
    public void onCall(XiaomingUser user) {
        if (getXiaomingBot().getCenterClient().isConnected()) {
            user.sendMessage("{lang.callNumberWithTheTotal}");
        } else {
            user.sendMessage("{lang.callNumber}");
        }
    }

//    /** 批处理指令 */
//    @Filter("#" + BATCH + "#" + "{r:指令}")
//    public void onMultipleCommands(XiaomingUser user,
//                                   @FilterParameter("指令") String remain,
//                                   Message message) {
//        final String[] subCommands = remain.split(Pattern.quote("\\n"), 0);
//        final long maxJoinTime = TimeUnit.SECONDS.toMillis(1);
//
//        user.enablePrintWriter();
//        try {
//            Future<Boolean> task = null;
//            for (int i = 1; i < subCommands.length; i++) {
//                String command = subCommands[i];
//                if (command.isEmpty()) {
//                    continue;
//                }
//
//                if (Objects.isNull(task) || task.isDone()) {
//                    final int finalI = i;
//                    task = getXiaomingBot().getScheduler().run(() -> {
//                        return getXiaomingBot().getInteractorManager().interact(user, command[finalI]);
//                    });
//                } else {
//                    user.onNextInput(command);
//                }
//            }
//            if (Objects.nonNull(task) && !task.isDone()) {
//                task.get();
//            }
//        } catch (Exception exception) {
//            user.sendError("{lang.batchTaskInterruptedByException}", exception);
//            exception.printStackTrace();
//        }
//        final String buffer = user.getBufferAndClose();
//        user.sendMessage(StringUtility.isEmpty(buffer) ? "{lang.batchTaskNoAnyOutput}" : buffer);
//    }

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
            if (Objects.equals(user.nextMessageOrExit().serialize(), "确定")) {
                preservables.clear();
                user.sendMessage("{lang.savePlanCancelled}");
            } else {
                user.sendMessage("{lang.savePlanNoCancelled}");
            }
        }
    }

    @Filter(CommandWords.ECHO + " {r:复读内容}")
    @Permission("core.echo")
    public void onEcho(XiaomingUser user, @FilterParameter("复读内容") String content) {
        if (StringUtility.nonEmpty(content)) {
            user.sendMessage(content);
        }
    }

    @Filter(CommandWords.COMMAND + CommandWords.FORMAT)
    @Filter(CommandWords.FORMAT)
    public void onGlobalUsage(XiaomingUser user) {
        final List<InteractorHandler> interactors = getXiaomingBot().getInteractorManager().getInteractors();
        final List<String> commandFormats = interactors.stream()
                .map(InteractorHandler::getUsage)
                .filter(StringUtility::nonEmpty)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (user instanceof ConsoleXiaomingUser) {
            user.sendMessage(CollectionUtility.toIndexString(commandFormats));
        } else {
            InteractorUtility.showCollection(user, commandFormats, String::toString, 30);
        }
    }

    @Filter(CommandWords.COMMAND + CommandWords.FORMAT + " {插件名}")
    public void onPluginUsage(XiaomingUser user, @FilterParameter("插件名") Plugin plugin) {
        final List<InteractorHandler> interactors = getXiaomingBot().getInteractorManager().getInteractors(plugin);
        final List<String> commandFormats = interactors.stream()
                .map(InteractorHandler::getUsage)
                .filter(StringUtility::nonEmpty)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (user instanceof ConsoleXiaomingUser) {
            user.sendMessage(CollectionUtility.toIndexString(commandFormats));
        } else {
            InteractorUtility.showCollection(user, commandFormats, String::toString, 30);
        }
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

        if (Objects.equals(user.nextMessageOrExit(TimeUnit.MINUTES.toMillis(1)).serialize(), "确定")) {
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
        final Receptionist receptionist = receptionistManager.getReceptionist(qq);

        if (Objects.isNull(receptionist)) {
            user.sendMessage("{lang.userHasNotReceptionist}");
        } else {
            user.sendMessage("{lang.userReceptionistDetail}", receptionist);
        }
    }

    @Filter(CommandWords.XIAOMING + CommandWords.STATUS)
    @Permission("core.status")
    public void onStatus(XiaomingUser user) {
        user.sendMessage( "{lang.xiaomingStatus}");
    }
}