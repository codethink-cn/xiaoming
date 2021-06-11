package com.chuanwise.xiaoming.core.thread;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.core.object.ModuleObjectImpl;
import com.chuanwise.xiaoming.core.recept.ReceptionistImpl;
import com.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
import lombok.Data;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class ConsoleInputThread extends ModuleObjectImpl implements Runnable {
    boolean warned = false;

    ConsoleXiaomingUser consoleUser;
    Receptionist consoleReceptionist;

    volatile Thread inputThread;

    public ConsoleInputThread(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    public void setConsoleUser(ConsoleXiaomingUser consoleUser) {
        this.consoleUser = consoleUser;
        consoleReceptionist = new ReceptionistImpl(consoleUser);
    }

    @Override
    public void run() {
        if (Objects.isNull(inputThread)) {
            inputThread = Thread.currentThread();
        } else {
            throw new XiaomingRuntimeException("multiple console input thread");
        }

        final Bot miraiBot = getXiaomingBot().getMiraiBot();

        try (Scanner scanner = new Scanner(System.in)) {
            while (!getXiaomingBot().isStop()) {
                final String message = scanner.nextLine();
                try {
                    consoleReceptionist.onPrivateMessage(getXiaomingBot().getContactManager().getPrivateContact(consoleUser.getCode()),
                            MessageChain.deserializeFromJsonString(message));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
