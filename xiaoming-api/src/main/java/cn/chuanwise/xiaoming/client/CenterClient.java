package cn.chuanwise.xiaoming.client;

import cn.chuanwise.api.Slf4jLoggerSendable;
import cn.chuanwise.exception.UnsupportedVersionException;
import cn.chuanwise.toolkit.functional.throwable.ThrowableRunnable;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.center.client.AbstractNormalClient;
import cn.chuanwise.xiaoming.center.client.CenterClientConfiguration;
import cn.chuanwise.xiaoming.center.BotLoginProtocol;
import cn.chuanwise.xiaoming.center.content.LoggerGroupConfirm;
import cn.chuanwise.xiaoming.center.content.NormalLoginContent;
import cn.chuanwise.xiaoming.center.content.remote.ConfirmResult;
import cn.chuanwise.xiaoming.object.ModuleObject;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Future;

public class CenterClient extends AbstractNormalClient implements ModuleObject, Slf4jLoggerSendable {
    public static final String HOST = "server.taixue.cc";
    public static final int PORT = 10075;

    @Getter
    final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    @Getter
    @Setter
    transient XiaomingBot xiaomingBot;

    public CenterClient(XiaomingBot xiaomingBot) {
        super(new CenterClientConfiguration(HOST, PORT));
        this.xiaomingBot = xiaomingBot;

        try {
            getLogger().info("正在连接小明中心服务器");
            if (connect()) {
                getLogger().info("连接成功");
            } else {
                getLogger().info("连接失败");
            }
        } catch (Exception exception) {
            getLogger().info("连接时出现异常", exception);
        }
    }

    @Override
    protected NormalLoginContent buildNormalLoginContent() {
        final BotLoginProtocol loginProtocol;
        switch (xiaomingBot.getMiraiBot().getConfiguration().getProtocol()) {
            case ANDROID_PAD:
                loginProtocol = BotLoginProtocol.ANDROID_PAD;
                break;
            case ANDROID_PHONE:
                loginProtocol = BotLoginProtocol.ANDROID_PHONE;
                break;
            case ANDROID_WATCH:
                loginProtocol = BotLoginProtocol.ANDROID_WATCH;
                break;
            default:
                throw new UnsupportedVersionException();
        }

        return new NormalLoginContent(loginProtocol,
                xiaomingBot.getCode(),
                xiaomingBot.getLanguageManager().format("{lang.xiaoming}"),
                XiaomingBot.VERSION_TYPE,
                XiaomingBot.VERSION);
    }

    @Override
    protected boolean isInGroup(long group) {
        return Objects.nonNull(xiaomingBot.getContactManager().getGroupContact(group));
    }

    @Override
    public void onDisconnectedAutomatically(Future<Boolean> reconnectFuture) throws Exception {
        getLogger().info("中心服务器断开了和小明的连接");
        if (Objects.nonNull(reconnectFuture)) {
            getLogger().info("重连请求已提交");
        } else {
            getLogger().info("未提交重连请求");
        }
    }

    @Override
    protected ConfirmResult loggerGroupConfirm(LoggerGroupConfirm request) {
        return null;
    }

    @Override
    protected void run(Runnable runnable) {
        xiaomingBot.getScheduler().run(runnable);
    }

    @Override
    public void logDebug(String message) {
        if (Objects.nonNull(xiaomingBot) && xiaomingBot.getConfiguration().isDebug()) {
            getLogger().info(message);
        }
    }

    public boolean doOrFail(ThrowableRunnable<Exception> runnable, String operation) {
        try {
            if (isConnected()) {
                runnable.throwableRun();
                return true;
            }
        } catch (Exception exception) {
            getLogger().error(operation + "失败", exception);
        }
        return false;
    }

    @Override
    public void execute(Runnable runnable) {
        xiaomingBot.getScheduler().run(runnable);
    }
}
