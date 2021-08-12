package cn.chuanwise.xiaoming.thread;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.message.ConsoleMessage;
import cn.chuanwise.xiaoming.exception.XiaomingRuntimeException;
import cn.chuanwise.xiaoming.interactor.Interactor;
import cn.chuanwise.xiaoming.recept.Receptionist;
import cn.chuanwise.xiaoming.contact.message.ConsoleMessageImpl;
import cn.chuanwise.xiaoming.log.ConsoleLogger;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.code.MiraiCode;

import java.io.File;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Future;

@Getter
@Setter
public class ConsoleInputThread extends ModuleObjectImpl implements Runnable {
    boolean warned = false;

    ConsoleXiaomingUser consoleUser;
    Receptionist consoleReceptionist;

    volatile Thread inputThread;
    ConsoleLogger consoleLogger;

    public ConsoleInputThread(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    public void setConsoleUser(ConsoleXiaomingUser consoleUser) {
        this.consoleUser = consoleUser;
        consoleReceptionist = consoleUser.getReceptionist();
    }

    public ConsoleLogger getConsoleLogger() {
        if (Objects.isNull(consoleLogger)) {
            consoleLogger = getXiaomingBot().getFileLoader()
                    .loadOrSupply(ConsoleLogger.class, new File(getXiaomingBot().getLogDirectory(), "console.json"), ConsoleLogger::new);
        }
        return consoleLogger;
    }

    @Override
    public void run() {
        if (Objects.isNull(inputThread)) {
            inputThread = Thread.currentThread();
        } else {
            throw new XiaomingRuntimeException("multiple console input thread");
        }

        Scanner scanner = new Scanner(System.in);
        while (!getXiaomingBot().isStop()) {
            final String message = scanner.nextLine();
            final ConsoleMessage consoleMessage = new ConsoleMessageImpl(consoleUser, MiraiCode.deserializeMiraiCode(message));

            try {
                final Interactor interactor = consoleUser.getInteractor();

                if (Objects.isNull(interactor)) {
                    // 启动新的接待任务
                    final Future<Boolean> future = getXiaomingBot().getScheduler().run(() -> {
                        Thread.currentThread().setName("reception-task[console]");
                        return getXiaomingBot().getInteractorManager().onInput(consoleUser, consoleMessage);
                    });

                    if (!future.get()) {
                        consoleUser.sendError("小明不知道你的意思");
                    }
                } else {
                    consoleUser.onNextInput(consoleMessage);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
