package cn.chuanwise.xiaoming.launcher;

import cn.chuanwise.util.ArgumentUtil;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.bot.XiaomingBotImpl;
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
    public Logger getLogger() {
        return log;
    }
}
