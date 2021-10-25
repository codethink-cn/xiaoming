package cn.chuanwise.xiaoming.listener;

import cn.chuanwise.util.*;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.annotation.EventListener;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.contact.message.MessageImpl;
import cn.chuanwise.xiaoming.event.InteractorErrorEvent;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.event.SendMessageEvent;
import cn.chuanwise.xiaoming.event.SimpleListeners;
import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.interactor.handler.Interactor;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class CoreListeners extends SimpleListeners {
    @EventListener
    public void onFriendAddRequest(NewFriendRequestEvent event) {
        if (getXiaomingBot().getConfiguration().isAutoAcceptFriendAddRequest()) {
            event.accept();
        }
    }

    @EventListener
    public void onGroupInvite(BotInvitedJoinGroupRequestEvent event) {
        if (getXiaomingBot().getConfiguration().isAutoAcceptGroupInvite()) {
            event.accept();
        }
    }

    @EventListener
    public void onSendMessage(SendMessageEvent event) throws InterruptedException, ExecutionException {
        xiaomingBot.getContactManager().readyToSend(event).get();
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

    @EventListener
    public void onInteractorError(InteractorErrorEvent event) {
        final InteractorContext context = event.getContext();
        final Throwable throwable = event.getThrowable();
        final XiaomingUser user = context.getUser();

        // save error log
        final File errorLog = new File(xiaomingBot.getReportDirectory(), "error-" + dateFormat.format(System.currentTimeMillis()) + ".txt");
        try {
            errorLog.createNewFile();
            try (OutputStream outputStream = new FileOutputStream(errorLog)) {
                final PrintStream stream = new PrintStream(outputStream);
                final Account account = context.getUser().getAccount();
                final Message message = context.getMessage();
                final Interactor interactor = context.getInteractor();

                final Map<String, Object> configurations = new HashMap<>();
                ReflectUtil.forEachDeclaredFieldValue(xiaomingBot.getConfiguration(), configurations::put,
                        false, false, true);

                stream.print("【错误报告】\n" +
                        "交互器交互时出现异常\n" +
                        "\n" +
                        "【异常概述】\n" +
                        "异常时间：" + TimeUtil.formatNow() + "\n" +
                        "触发人：" + context.getUser().getCompleteName() + "\n" +
                        "交互器：" + interactor.getName() + "\n" +
                        "交互类：" + interactor.getMethod().getDeclaringClass().getName() + "\n" +
                        "注册方：" + Plugin.getChineseName(context.getPlugin()) + "\n" +
                        "触发消息：" + message.serialize() + "\n" +
                        "原始消息：" + message.serializeOriginalMessage() + "\n" +
                        "\n" +
                        "【异常信息】\n" +
                        ThrowableUtil.toStackTraces(throwable) +
                        "\n" +
                        "【详细信息】\n" +
                        "用户标签：" + CollectionUtil.toString(context.getUser().getTags()) + "\n" +
                        "为封禁用户：" + account.isBanned() + "\n" +
                        "为管理员用户：" + account.isAdministrator() + "\n" +
                        "过滤器参数：" + context.getArguments() + "\n" +
                        "交互器方法：" + interactor.getMethod() + "\n" +
                        "交互器方法参数：" + context.getFinalArguments() + "\n" +
                        "\n" +
                        "【小明信息】\n" +
                        "内核版本：" + XiaomingBot.VERSION + "\n" +
                        "启动时间：" + TimeUtil.format(xiaomingBot.getStatistician().getBeginTime()) + "\n" +
                        "运行时长：" + TimeUtil.toTimeLength(System.currentTimeMillis() - xiaomingBot.getStatistician().getBeginTime()) + "\n" +
                        "插件信息：" + Optional.ofNullable(CollectionUtil.toIndexString(xiaomingBot.getPluginManager().getPlugins().values(), p -> {
                                            return p.getCompleteName() + "（" + p.getStatus().toChinese() + "）";
                                        }))
                                        .map(x -> "\n" + x)
                                        .orElse("（无）") + "\n" +
                        "核心配置：" + "\n" +
                        CollectionUtil.toIndexString(configurations.entrySet(), entry -> entry.getKey() + " = " + entry.getValue()) + "\n" +
                        "\n" +
                        "【环境信息】\n" +
                        "JDK 版本：" + System.getProperty("java.version") + "\n" +
                        "JVM 名称：" + System.getProperty("java.vm.name") + "\n" +
                        "OS 名称：" + System.getProperty("os.name")
                );
            }
            user.sendError("{lang.internalError}", errorLog.getName());
        } catch (IOException exception) {
            exception.printStackTrace();
            user.sendError("{lang.internalErrorButLogNoSaved}");
        }
        getLogger().error("和用户 " + user.getCompleteName() + " 交互时出现异常", throwable);
    }
}