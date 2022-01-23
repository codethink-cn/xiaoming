package cn.chuanwise.xiaoming.launcher;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import org.slf4j.Logger;

import java.io.IOException;

public interface XiaomingLauncher {
    /**
     * 载入一大堆设置
     * @return
     */
    boolean launch();

    /**
     * 启动小明
     */
    default void start() throws Exception {
        getXiaomingBot().start();
        setShutdownHook();
    }

    default void setShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 如果小明此时还没有关闭则关闭
            if (!getXiaomingBot().isDisabled()) {
                getXiaomingBot().stop();
            }
        }));
    }

    /**
     * 关闭小明
     */
    void stop();

    XiaomingBot getXiaomingBot();

    Logger getLogger();
}
