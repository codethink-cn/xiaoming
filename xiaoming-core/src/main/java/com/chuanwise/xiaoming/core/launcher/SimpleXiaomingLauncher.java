package com.chuanwise.xiaoming.core.launcher;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.launcher.XiaomingLauncher;
import com.chuanwise.xiaoming.api.util.ArgumentUtils;
import com.chuanwise.xiaoming.core.bot.XiaomingBotImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
@Getter
@AllArgsConstructor
public class SimpleXiaomingLauncher implements XiaomingLauncher {
    final XiaomingBot xiaomingBot;

    @Override
    public boolean launch() {
        return true;
    }

    @Override
    public void stop() {
        xiaomingBot.stop();
    }

    @Override
    public Logger getLog() {
        return log;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("参数：[qq] [password]");
            return;
        }

        final long qq;
        if (args[0].matches("\\d+")) {
            qq = Long.parseLong(args[0]);
        } else {
            System.err.println("qq 格式错误：" + args[0]);
            return;
        }

        final String password = ArgumentUtils.getReaminArgs(args, 1);
        final SimpleXiaomingLauncher launcher = new SimpleXiaomingLauncher(new XiaomingBotImpl(qq, password));

        launcher.launch();
        launcher.start();
    }
}
