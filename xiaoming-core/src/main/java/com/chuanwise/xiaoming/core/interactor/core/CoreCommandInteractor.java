package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.interactor.InteractorManager;
import com.chuanwise.xiaoming.api.interactor.command.CommandInteractor;
import com.chuanwise.xiaoming.api.config.Configuration;
import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.license.LicenseManager;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.runnable.RegularPreserveManager;
import com.chuanwise.xiaoming.api.text.TextManager;
import com.chuanwise.xiaoming.api.user.Receiptionist;
import com.chuanwise.xiaoming.api.user.ReceiptionistManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.api.util.TimeUtil;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;

import java.util.*;
import java.util.concurrent.TimeUnit;
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

    @Filter(CommandWords.INTERACTOR_REGEX)
    @RequirePermission("interactor.list")
    public void onInteractorStatus(XiaomingUser user) {
        user.sendPrivateMessage(getInteractorString());
    }

    @Filter(CommandWords.CALL_REGEX)
    public void onCallCounter(XiaomingUser user) {
        user.sendMessage("小明至今的召唤次数：{}", getXiaomingBot().getStatistician().getCallNumber());
    }

    @Filter(CommandWords.EXCEPTION_REGEX)
    @RequirePermission("debug.exception")
    public void onThrowException(XiaomingUser user) {
        final XiaomingRuntimeException exception = new XiaomingRuntimeException();
        user.sendMessage("小明将尝试抛出异常：{}", exception.getClass().getSimpleName());
        throw exception;
    }

    /**
     * 批处理指令
     * @param user 指令执行者
     * @param remain 指令
     */
    @Filter("##" + BAT_REGEX + "##" + "{remain}")
    public void onMultipleCommands(XiaomingUser user,
                                   @FilterParameter("remain") String remain) {
        final String[] subCommands = remain.split(Pattern.quote("\\n"), 0);

        user.enableBuffer();
        int commandNumber = 0;
        try {
            for (int i = 0; i < subCommands.length; i++) {
                String command = subCommands[i];
                if (command.isEmpty()) {
                    continue;
                }
                user.setMessage(command);
                if (getXiaomingBot().getInteractorManager().onInput(user)) {
                    commandNumber++;
                } else {
                    user.sendError("无效的命令：{}，批处理任务被中断", command);
                    break;
                }
            }
        } catch (Exception exception) {
            user.sendError("执行{}个指令时出现异常，批处理任务被中断");
            exception.printStackTrace();
        }

        final String bufferString = user.getBufferAndClear();
        user.sendPrivateMessage(bufferString);

        if (commandNumber == 0) {
            user.sendError("小明没能成功执行任何一个指令");
        } else {
            user.sendMessage("成功执行了 {} 个指令", commandNumber);
        }
    }

    @Filter(CommandWords.SAVE_REGEX)
    @RequirePermission("save")
    public void onSave(XiaomingUser user) {
        final RegularPreserveManager manager = getXiaomingBot().getRegularPreserveManager();
        if (manager.getPreservables().isEmpty()) {
            user.sendMessage("当前没有任何文件等待保存哦");
        } else {
            manager.save(user);
        }
    }

    @Filter(CommandWords.ENABLE_REGEX + CommandWords.USE_REGEX + "(验证|verify)")
    @RequirePermission("license.enable")
    public void onEnableUseVerify(XiaomingUser user) {
        final Configuration config = getXiaomingBot().getConfig();
        if (config.isEnableLicense()) {
            user.sendMessage("强制小明使用验证本就是启动的");
        } else {
            final TextManager textManager = getXiaomingBot().getTextManager();
            final String licenseName = config.getLicenseName();
            String license = textManager.load(licenseName);
            if (Objects.isNull(license)) {
                user.sendMessage("你还没有设置《小明使用协议》，告诉小明它的内容吧");
                license = user.nextInput();
                textManager.save(licenseName, license);
            }
            config.enableLisence();
            user.sendMessage("已启动强制小明使用验证");
            getXiaomingBot().getRegularPreserveManager().readySave(config);
        }
    }

    @Filter(CommandWords.DISABLE_REGEX + CommandWords.USE_REGEX + "(验证|verify)")
    @RequirePermission("license.disable")
    public void onDisableUseVerify(XiaomingUser user) {
        final Configuration config = getXiaomingBot().getConfig();
        if (config.isEnableLicense()) {
            config.disableLisence();
            getXiaomingBot().getRegularPreserveManager().readySave(config);
            user.sendMessage("已关闭强制小明使用验证");
        } else {
            user.sendMessage("强制小明使用验证并没有开启哦");
        }
    }

    @Filter("(指令格式|格式|usage|format)")
    public void onGlobalUsage(XiaomingUser user) {
        List<String> runnableCoreCommands = new ArrayList<>();
        final InteractorManager interactorManager = getXiaomingBot().getInteractorManager();

        // 获取内核帮助
        for (Interactor interactor : interactorManager.getCoreInteractors()) {
            if (interactor instanceof CommandInteractor) {
                runnableCoreCommands.addAll(((CommandInteractor) interactor).getUsageStrings(user));
            }
        }
        // 获取插件帮助
        for (Map.Entry<XiaomingPlugin, Set<Interactor>> entry : interactorManager.getPluginInteractors().entrySet()) {
            final XiaomingPlugin plugin = entry.getKey();
            final Set<Interactor> interactors = entry.getValue();

            if (!user.isBlockPlugin(plugin.getName())) {
                for (Interactor interactor : interactors) {
                    if (interactor instanceof CommandInteractor) {
                        runnableCoreCommands.addAll(interactor.getUsageStrings(user));
                    }
                }
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

        user.sendPrivateMessage(builder.toString());
    }

    @Filter(CommandWords.HELP_REGEX)
    public void onGlobalHelp(XiaomingUser user) {
        user.sendMessage("欢迎使用小明！\n" +
                "你可用的所有小明指令可以通过 #指令格式 查询" + getXiaomingBot().getWordManager().get("happy"));

        user.sendMessage("如果你觉得小明很不错，欢迎到 https://github.com/TaixueChina/xiaoming-bot 给我们点亮一颗星星\n" +
                "如果在使用途中小明对你造成了困扰，或者希望邀请小明来你的群，欢迎私聊椽子（QQ：1437100907）或者去上述 Github 提 issue\n" +
                "小明正在学习的技能有：百科词条、和MC服务器互通。期待更好的小明把~\n" +
                "如果你想要编写小明的功能，欢迎打开上述链接查看开发文档。");
    }

    long lastCloseConfirmTime;

    @Filter(CommandWords.DISABLE_REGEX + CommandWords.XIAOMING_REGEX)
    @RequirePermission("stop")
    public void onCloseXiaoming(XiaomingUser user) {
        user.sendMessage("你确定要关闭小明吗？如果是，请在一分钟之内发送「确定关闭小明」");
        lastCloseConfirmTime = System.currentTimeMillis() + TimeUtil.MINUTE_MINS;
    }

    @Filter(CommandWords.CONFIRM_REGEX + CommandWords.DISABLE_REGEX + CommandWords.XIAOMING_REGEX)
    @RequirePermission("stop")
    public void onConfirmCloseXiaoming(XiaomingUser user) {
        if (lastCloseConfirmTime > System.currentTimeMillis()) {
            getXiaomingBot().stop(user);
        } else {
            user.sendError("没有需要确认的小明关闭操作");
        }
    }

    @Filter(CommandWords.RECEIPTION_REGEX)
    @RequirePermission("receiptionist")
    public void onReceiptionist(XiaomingUser user) {
        final ReceiptionistManager receiptionistManager = getXiaomingBot().getReceiptionistManager();
        final Collection<Receiptionist> receiptionists = receiptionistManager.getReceiptionists().values();

        if (receiptionists.isEmpty()) {
            user.sendMessage("当前无任何招待器");
        } else {
            user.enableBuffer();
            user.sendMessage("当前共有 {} 个招待器：", receiptionists.size());
            for (Receiptionist receiptionist : receiptionists) {
                final XiaomingUser receiptionistUser = receiptionist.getUser();
                user.sendMessage(String.valueOf(receiptionistUser.getQQ()));
            }
            final String string = user.getBufferAndClear();
            user.sendMessage(string);
        }
    }

    @Filter(CommandWords.RECEIPTION_REGEX + " {qq}")
    @RequirePermission("receiptionist")
    public void onReceiptionist(XiaomingUser user, @FilterParameter("qq") long qq) {
        final ReceiptionistManager receiptionistManager = getXiaomingBot().getReceiptionistManager();
        final Receiptionist receiptionist = receiptionistManager.getReceiptionist(qq);

        if (Objects.isNull(receiptionist)) {
            user.sendMessage("该用户并没有招待器");
        } else {
            user.sendMessage("该招待器{}", receiptionist.isReceipting() ? "忙碌" : "空闲");
        }
    }

    @Filter(CommandWords.DISABLE_REGEX + CommandWords.RECEIPTION_REGEX + " {qq}")
    @RequirePermission("receiptionist.disable")
    public void onDisableReceiptionist(XiaomingUser user, @FilterParameter("qq") long qq) {
        final ReceiptionistManager receiptionistManager = getXiaomingBot().getReceiptionistManager();
        final Receiptionist receiptionist = receiptionistManager.getReceiptionist(qq);

        if (Objects.isNull(receiptionist)) {
            user.sendMessage("该用户并没有招待器");
        } else {
            receiptionist.stop();
            user.sendMessage("已尝试销毁该接待员");
        }
    }
}