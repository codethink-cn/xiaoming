package com.chuanwise.xiaoming.core.thread;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.util.StringUtils;
import com.chuanwise.xiaoming.core.contact.message.ConsoleMessageImpl;
import com.chuanwise.xiaoming.core.log.ConsoleLogger;
import com.chuanwise.xiaoming.core.object.ModuleObjectImpl;
import com.chuanwise.xiaoming.core.recept.ReceptionistImpl;
import com.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.FileMessage;
import net.mamoe.mirai.message.data.MessageChain;

import java.io.File;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            consoleLogger = getXiaomingBot().getFilePreservableFactory()
                    .loadOrProduce(ConsoleLogger.class, new File(getXiaomingBot().getLogDirectory(), "console.json"), ConsoleLogger::new);
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

        try (Scanner scanner = new Scanner(System.in)) {
            while (!getXiaomingBot().isStop()) {
                final String message = scanner.nextLine();
                final ConsoleMessageImpl consoleMessage = new ConsoleMessageImpl(consoleUser, MiraiCode.deserializeMiraiCode(message));
                try {
                    final Interactor interactor = consoleUser.getInteractor();
                    if (Objects.isNull(interactor)) {
                        getXiaomingBot().getScheduler().run(() -> {
                            if (!getXiaomingBot().getInteractorManager().onInput(consoleUser, consoleMessage)) {
                                Thread.currentThread().setName("reception-task[console]");
                                consoleUser.sendError("小明不知道你的意思");
                            }
                            return true;
                        }).setDescription("控制台接待任务");
                    } else {
                        consoleUser.onNextInput(consoleMessage);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
