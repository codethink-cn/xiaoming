package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.MapUtil;
import cn.chuanwise.util.StringUtil;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.interactor.InteractorManager;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.interactor.handler.Interactor;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.schedule.FileSaver;
import cn.chuanwise.xiaoming.recept.Receptionist;
import cn.chuanwise.xiaoming.recept.ReceptionistManager;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;
import cn.chuanwise.xiaoming.util.InteractorUtil;
import cn.chuanwise.xiaoming.util.MessageUtil;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.ForwardMessage;
import net.mamoe.mirai.message.data.MessageChain;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CoreInteractors extends SimpleInteractors {
    public static final String BATCH = "(批处理|bat)";

    @Filter(CommandWords.INTERACTOR)
    @Permission("core.interactor.list")
    public void onInteractor(XiaomingUser user) {
        final Map<String, List<String>> interactorDetails = new HashMap<>();

        final InteractorManager interactorManager = getXiaomingBot().getInteractorManager();
        interactorManager.getInteractors().forEach(interactor -> {
            final List<String> interactorNames = MapUtil.getOrPutSupply(interactorDetails, Plugin.getEnglishName(interactor.getPlugin()), ArrayList::new);
            interactorNames.add(interactor.getInteractors().getClass().getSimpleName() + "." + interactor.getName());
        });

        user.sendMessage("{lang.interactors}", interactorDetails);
    }

    @Filter(CommandWords.CALL)
    @Permission("core.statistics.call")
    public void onCall(XiaomingUser user) {
        user.sendMessage("{lang.callNumber}");
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
//        user.sendMessage(StringUtil.isEmpty(buffer) ? "{lang.batchTaskNoAnyOutput}" : buffer);
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
        final Map<File, Preservable> preservables = fileSaver.getPreservables();
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
        final Map<File, Preservable> preservables = task.getPreservables();

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
        if (StringUtil.notEmpty(content)) {
            user.sendMessage(content);
        }
    }

    @Filter(CommandWords.COMMAND + CommandWords.FORMAT)
    public void onGlobalUsage(XiaomingUser user) {
        final List<Interactor> interactors = getXiaomingBot().getInteractorManager().getInteractors();
        final List<String> commandFormats = interactors.stream()
                .map(Interactor::getUsage)
                .filter(StringUtil::notEmpty)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (user instanceof ConsoleXiaomingUser) {
            user.sendMessage(CollectionUtil.toIndexString(commandFormats));
        } else {
            InteractorUtil.showCollection(user, commandFormats, String::toString, 30);
        }
    }

    @Filter(CommandWords.COMMAND + CommandWords.FORMAT + " {r:插件}")
    public void onPluginUsage(XiaomingUser user, @FilterParameter("插件") Plugin plugin) {
        final List<Interactor> interactors = getXiaomingBot().getInteractorManager().getInteractors(plugin);
        final List<String> commandFormats = interactors.stream()
                .map(Interactor::getUsage)
                .filter(StringUtil::notEmpty)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (user instanceof ConsoleXiaomingUser) {
            user.sendMessage(CollectionUtil.toIndexString(commandFormats));
        } else {
            InteractorUtil.showCollection(user, commandFormats, String::toString, 30);
        }
    }

    @Filter(CommandWords.SEARCH + CommandWords.COMMAND + " {r:关键字}")
    public void searchCommands(XiaomingUser user, @FilterParameter("关键字") String keyword) {
        final List<Interactor> interactors = getXiaomingBot().getInteractorManager().getInteractors(plugin);
        final List<String> commandFormats = interactors.stream()
                .map(Interactor::getUsage)
                .filter(StringUtil::notEmpty)
                .filter(x -> x.contains(keyword) || keyword.contains(x))
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (commandFormats.isEmpty()) {
            user.sendError("没有用「" + keyword + "」搜索到任何指令");
            return;
        }

        if (user instanceof ConsoleXiaomingUser) {
            user.sendMessage(CollectionUtil.toIndexString(commandFormats));
        } else {
            user.sendMessage("用「" + keyword + "」搜索到以下指令：\n" +
                    CollectionUtil.toIndexString(commandFormats));
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