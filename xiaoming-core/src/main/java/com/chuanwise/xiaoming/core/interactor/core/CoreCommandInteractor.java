package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.Require;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.interactor.InteractorManager;
import com.chuanwise.xiaoming.api.configuration.Configuration;
import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.interactor.command.CommandInteractor;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.recept.GroupReceptionTask;
import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.thread.Finalizer;
import com.chuanwise.xiaoming.api.text.TextManager;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.recept.ReceptionistManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.StringUtils;
import com.chuanwise.xiaoming.api.util.TimeUtils;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;

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

    @Filter("(维护|调试|debug)")
    @Require("debug")
    public void onDebug(XiaomingUser user) {
        final Configuration config = getXiaomingBot().getConfiguration();
        config.setDebug(!config.isDebug());
        getXiaomingBot().getFinalizer().readySave(config);
        if (config.isDebug()) {
            user.sendMessage("已开启小明的维护状态");
        } else {
            user.sendMessage("已关闭小明的维护状态");
        }
    }

    @Filter(CommandWords.INTERACTOR)
    @Require("interactor.list")
    public void onInteractorStatus(XiaomingUser user) {
        user.sendMessage(getInteractorString());
    }

    @Filter(CommandWords.CALL)
    @Require("statistics.call")
    public void onCallCounter(XiaomingUser user) {
        user.sendMessage("小明至今的召唤次数：{}", getXiaomingBot().getStatistician().getCallNumber());
    }

    /**
     * 批处理指令
     * @param user 指令执行者
     * @param remain 指令
     */
    @Filter("#" + BAT_REGEX + "#" + "{remain}")
    public void onMultipleCommands(XiaomingUser user,
                                   @FilterParameter("remain") String remain) {
        final String[] subCommands = remain.split(Pattern.quote("\\n"), 0);

        user.enableBuffer();
        int commandNumber = 0;
        try {
            for (int i = 1; i < subCommands.length; i++) {
                String command = subCommands[i];
                if (command.isEmpty()) {
                    continue;
                }
                // user.getReceptionTask().onNextInput();
                /*
                if (getXiaomingBot().getInteractorManager().onInput(user)) {
                    commandNumber++;
                } else {
                    user.sendError("无效的命令：{}，批处理任务被中断", command);
                    break;
                }
                */
            }
        } catch (Exception exception) {
            user.sendError("执行{}个指令时出现异常，批处理任务被中断");
            exception.printStackTrace();
        }

        final String bufferString = user.getBufferAndClose();
        user.sendMessage(bufferString);

        if (commandNumber == 0) {
            user.sendError("小明没能成功执行任何一个指令");
        } else {
            user.sendMessage("成功执行了 {} 个指令", commandNumber);
        }
    }

    @Filter(CommandWords.SAVE)
    @Require("save")
    public void onSave(XiaomingUser user) {
        final Finalizer finalizer = getXiaomingBot().getFinalizer();
        if (finalizer.getPreservables().isEmpty()) {
            user.sendMessage("当前没有任何文件等待保存哦");
        } else {
            finalizer.save(user);
        }
    }

    @Filter(CommandWords.ENABLE + CommandWords.USE + "(验证|verify)")
    @Require("license.enable")
    public void onEnableUseVerify(XiaomingUser user) {
        final Configuration config = getXiaomingBot().getConfiguration();
        if (config.isEnableLicense()) {
            user.sendMessage("强制小明使用验证本就是启动的");
        } else {
            /*
            final TextManager textManager = getXiaomingBot().getTextManager();
            final String licenseName = config.getLicenseName();
            String license = textManager.load(licenseName);
            if (Objects.isNull(license)) {
                user.sendMessage("你还没有设置《小明使用协议》，告诉小明它的内容吧");
                license = user.nextInput();
                textManager.save(licenseName, license);
            }

             */
            config.enableLicence();
            user.sendMessage("已启动强制小明使用验证");
            getXiaomingBot().getFinalizer().readySave(config);
        }
    }

    @Filter(CommandWords.DISABLE + CommandWords.USE + "(验证|verify)")
    @Require("license.disable")
    public void onDisableUseVerify(XiaomingUser user) {
        final Configuration config = getXiaomingBot().getConfiguration();
        if (config.isEnableLicense()) {
            config.disableLicence();
            getXiaomingBot().getFinalizer().readySave(config);
            user.sendMessage("已关闭强制小明使用验证");
        } else {
            user.sendMessage("强制小明使用验证并没有开启哦");
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
    public void onGlobalHelp(XiaomingUser user) {
        user.sendMessage("欢迎使用小明！\n" +
                "你可用的所有小明指令可以通过「指令格式」查询 {happy}");

        user.sendMessage("如果你觉得小明很不错，欢迎到 https://github.com/TaixueChina/xiaoming-bot 给我们点亮一颗星星\n" +
                "如果在使用途中小明对你造成了困扰，或者希望邀请小明来你的群，欢迎私聊椽子（QQ：1437100907）或者去上述 Github 提 issue\n" +
                "小明正在学习的技能有：百科词条、和MC服务器互通。期待更好的小明把~\n" +
                "如果你想要编写小明的功能，欢迎打开上述链接查看开发文档。");
    }

    long lastCloseConfirmTime;

    @Filter(CommandWords.DISABLE + CommandWords.XIAOMING)
    @Require("stop")
    public void onCloseXiaoming(XiaomingUser user) {
        user.sendMessage("你确定要关闭整个小明程序吗？关闭后小明只能在后台被唤醒。如果是，请在一分钟之内发送「确定关闭小明」");
        lastCloseConfirmTime = System.currentTimeMillis() + TimeUtils.MINUTE_MINS;
    }

    @Filter(CommandWords.CONFIRM + CommandWords.DISABLE + CommandWords.XIAOMING)
    @Require("stop")
    public void onConfirmCloseXiaoming(XiaomingUser user) {
        if (lastCloseConfirmTime > System.currentTimeMillis()) {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException exception) {
            }
            getXiaomingBot().stop(user);
        } else {
            user.sendError("没有需要确认的小明关闭操作");
        }
    }

    @Filter(CommandWords.RECEPTION)
    @Require("receptionist")
    public void onReceptionist(XiaomingUser user) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();
        final Collection<Receptionist> receptionists = receptionistManager.getReceptionists().values();

        if (receptionists.isEmpty()) {
            user.sendMessage("当前无任何接待员");
        } else {
            user.sendMessage("当前共有 {} 个接待员：{}",
                    receptionists.size(),
                    StringUtils.getCollectionSummary(receptionists, receptionist -> {
                        return receptionist.getCode() + "：" + (receptionist.isBusy() ? "忙碌" : "空闲");
                    }));
        }
    }

    @Filter(CommandWords.RECEPTION + " {qq}")
    @Require("receptionist")
    public void onReceptionist(XiaomingUser user, @FilterParameter("qq") long qq) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();
        final Receptionist receptionist = receptionistManager.getReceptionist(qq);

        if (Objects.isNull(receptionist)) {
            user.sendMessage("该用户并没有接待员");
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
    @Require("receptionist.disable")
    public void onDisableReceptionist(XiaomingUser user, @FilterParameter("qq") long qq) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();
        final Receptionist receptionist = receptionistManager.getReceptionist(qq);

        if (Objects.isNull(receptionist)) {
            user.sendMessage("该用户并没有接待员");
        } else {
            if (receptionist.isBusy()) {
                receptionist.optimize();
                user.sendMessage("接待员忙碌。已终止所有空闲接待线程以释放资源。如仍要强制关闭，请使用「强制关闭接待员 {}」", qq);
            } else {
                receptionist.stop();
                user.sendMessage("已尝试销毁该接待员");
            }
        }
    }

    @Filter(CommandWords.OPTIMIZE + CommandWords.RECEPTION + " {qq}")
    @Require("receptionist.optimize")
    public void onOptimizeReceptionist(XiaomingUser user, @FilterParameter("qq") long qq) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();
        final Receptionist receptionist = receptionistManager.getReceptionist(qq);

        if (Objects.isNull(receptionist)) {
            user.sendMessage("该用户并没有接待员");
        } else {
            receptionist.optimize();
            user.sendMessage("已优化该用户的接待员", qq);
        }
    }

    @Filter(CommandWords.OPTIMIZE + CommandWords.RECEPTION)
    @Require("receptionist.optimize")
    public void onOptimizeReceptionist(XiaomingUser user) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();

        receptionistManager.optimize();
        user.sendMessage("已优化可能优化的所有接待员");
    }

    @Filter("(强制|force)" + CommandWords.DISABLE + CommandWords.RECEPTION + " {qq}")
    @Require("receptionist.disable")
    public void onForceDisableReceptionist(XiaomingUser user, @FilterParameter("qq") long qq) {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();
        final Receptionist receptionist = receptionistManager.getReceptionist(qq);

        if (Objects.isNull(receptionist)) {
            user.sendMessage("该用户并没有接待员");
        } else {
            user.sendMessage("已尝试销毁该接待员");
        }
    }

    @Filter(CommandWords.XIAOMING + CommandWords.STATUS)
    @Require("status")
    public void onStatus(XiaomingUser user) {
        final long lastSaveTime = getXiaomingBot().getFinalizer().getLastSaveTime();
        StringBuilder builder = new StringBuilder();

        builder.append("小明启动于：").append(TimeUtils.FORMAT.format(getXiaomingBot().getLastStartTime())).append("\n")
                .append("已运行：").append(TimeUtils.toTimeString(System.currentTimeMillis() - getXiaomingBot().getLastStartTime())).append("\n")
                .append("上次文件保存时间：").append(TimeUtils.FORMAT.format(lastSaveTime)).append("\n")
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