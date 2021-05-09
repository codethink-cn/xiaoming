package com.chuanwise.xiaoming.host.runnable;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
import com.chuanwise.xiaoming.core.object.HostXiaomingObjectImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Scanner;

public class ConsoleListenerRunnable extends HostXiaomingObjectImpl implements Runnable {
    public ConsoleListenerRunnable(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    @Override
    public void run() {
        final ConsoleXiaomingUser user = getXiaomingBot().getConsoleXiaomingUser();

        try {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
                while (!getXiaomingBot().isStop()) {
                    if (!bufferedReader.ready()) {
                        continue;
                    }

                    final String message = bufferedReader.readLine();

                    user.setMessage(message);
                    try {
                        if (!getXiaomingBot().getCommandManager().onCommand(user)) {
                            user.sendError("小明不知道你的意思 qwq");
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        } catch (IOException ignored) {
        }
    }
}
