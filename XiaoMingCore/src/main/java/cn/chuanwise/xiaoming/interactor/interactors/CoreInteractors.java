package cn.chuanwise.xiaoming.interactor.interactors;

import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.StringUtil;
import cn.chuanwise.util.TimeUtil;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Required;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.configuration.Statistician;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.exception.XiaomingException;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.interactor.handler.Interactor;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.schedule.FileSaver;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.PrivateXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;
import cn.chuanwise.xiaoming.util.InteractorUtil;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CoreInteractors extends SimpleInteractors {
    public static final String BATCH = "(批处理|bat)";

    @Filter(CommandWords.CALL)
    @Required("core.statistics.call")
    public void call(XiaomingUser user) {
        final Statistician statistician = xiaomingBot.getStatistician();
        user.sendMessage("小明至今的调用次数：" + statistician.getCallNumber() + "，其中 " + statistician.getEffectiveCallNumber() + " 次为有效调用");
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
    @Required("core.optimize")
    public void optimize(XiaomingUser user) {
        getXiaomingBot().getOptimizer().optimize();
        user.sendMessage("成功执行一次优化");
    }

    @Filter(CommandWords.SAVE)
    @Required("core.save.do")
    public void save(XiaomingUser user) {
        final FileSaver fileSaver = getXiaomingBot().getFileSaver();
        final Map<File, Preservable> preservables = fileSaver.getPreservables();
        final int sizeBeforeSave = preservables.size();

        if (sizeBeforeSave == 0) {
            user.sendMessage("没有任何文件在保存计划中");
        } else {
            fileSaver.save();
            if (preservables.isEmpty()) {
                user.sendMessage("成功保存了 " + sizeBeforeSave + " 个文件");
            } else if (sizeBeforeSave == preservables.size()) {
                user.sendError("没有成功保存任何文件\n" +
                        "小明将在下一个文件保存周期或关闭前再次尝试保存这些文件：\n" +
                        CollectionUtil.toIndexString(preservables.values(), x -> x.getFile().getName()));
            } else {
                user.sendError("成功保存了 " + (sizeBeforeSave - preservables.size()) + " 个文件\n" +
                        "小明将在下一个文件保存周期或关闭前再次尝试保存这些文件：\n" +
                        CollectionUtil.toIndexString(preservables.values(), x -> x.getFile().getName()));
            }
        }
    }

    @Filter(CommandWords.CANCEL + CommandWords.SAVE)
    @Required("core.save.cancel")
    public void cancelSave(XiaomingUser user) {
        final FileSaver task = getXiaomingBot().getFileSaver();
        final Map<File, Preservable> preservables = task.getPreservables();

        if (preservables.isEmpty()) {
            user.sendMessage("没有任何文件在保存计划中");
        } else {
            user.sendWarning("真的要取消保存这 " + preservables.size() + " 个文件吗？这可能导致部分数据丢失！\n" +
                    "如果确定，请在一分钟之内回复「确定」，超时或其他回复将取消这次操作");
            if (Objects.equals(user.nextMessageOrExit().serialize(), "确定")) {
                preservables.clear();
                user.sendMessage("成功取消保存这些文件");
            } else {
                user.sendMessage("保存计划没有被取消");
            }
        }
    }

    @Filter(CommandWords.ECHO + " {r:复读内容}")
    @Required("core.echo")
    public void echo(XiaomingUser user, @FilterParameter("复读内容") String content) {
        if (StringUtil.notEmpty(content)) {
            user.sendMessage(content);
        }
    }

    @Filter(CommandWords.COMMAND + CommandWords.FORMAT)
    @Required("core.help")
    public void globalUsage(XiaomingUser user) {
        if (user instanceof GroupXiaomingUser) {
            user.sendError("指令格式较长，不允许在群聊里查看！");
            return;
        }

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

    @Filter(CommandWords.COMMAND + CommandWords.FORMAT + " {插件}")
    @Required("core.help")
    public void pluginUsage(XiaomingUser user, @FilterParameter("插件") Plugin plugin) {
        if (user instanceof GroupXiaomingUser) {
            user.sendError("指令格式较长，不允许在群聊里查看！");
            return;
        }

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
    @Required("core.help")
    public void searchCommands(XiaomingUser user, @FilterParameter("关键字") String keyword) {
        final List<Interactor> interactors = getXiaomingBot().getInteractorManager().getInteractors();
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
    @Required("core.help")
    public void help(XiaomingUser user) {
        user.sendMessage("欢迎使用小明！\n" +
                "你可用的所有指令可以通过「指令格式」查询。\n" +
                "如果你觉得小明很不错，欢迎到 " + XiaomingBot.GITHUB + " 给我们点亮一颗星星\n" +
                "也欢迎加入小明用户交流群：1028959718");
    }

    @Filter(CommandWords.DISABLE + CommandWords.XIAOMING)
    @Required("core.stop")
    public void closeXiaoming(XiaomingUser user) {
        user.sendMessage("你真的希望关闭整个小明程序吗？关闭后只能在控制台唤醒。\n" +
                "如果是，请在一分钟之内回复「确定」，超时或其他回复将取消本次操作");

        if (Objects.equals(user.nextMessageOrExit(TimeUnit.MINUTES.toMillis(1)).serialize(), "确定")) {
            final long delay = TimeUnit.SECONDS.toMillis(10);
            getXiaomingBot().getScheduler().runLater(delay, xiaomingBot::stop);
            user.sendMessage("小明将在 " + TimeUtil.toTimeLength(delay) +" 后自动关闭");
        } else {
            user.sendError("关闭小明操作被取消，你可以继续使用小明");
        }
    }

    @Filter(CommandWords.STOP)
    public void consoleClose(ConsoleXiaomingUser user) {
        user.sendMessage("小明将在 10 秒后关闭");
        xiaomingBot.getScheduler().runLater(TimeUnit.SECONDS.toMillis(10), xiaomingBot::stop);
    }

    @Filter(CommandWords.EXCEPTION + CommandWords.TEST)
    @Required("core.exception")
    public void exceptionTest(XiaomingUser user) throws XiaomingException {
        final XiaomingException xiaomingException = new XiaomingException();
        user.sendMessage("小明将抛出异常：" + XiaomingException.class.getSimpleName() + "，请不要反馈此错误！");
        throw xiaomingException;
    }

    @Filter(CommandWords.XIAOMING + CommandWords.STATUS)
    @Required("core.status")
    public void onStatus(XiaomingUser user) {
        final Statistician statistician = xiaomingBot.getStatistician();
        user.sendMessage( "小明启动于：" + TimeUtil.format(statistician.getBeginTime()) + "\n" +
                "已运行：" + TimeUtil.toTimeLength(System.currentTimeMillis() - statistician.getBeginTime()) + "\n" +
                "内核版本：" + XiaomingBot.VERSION);
    }
}