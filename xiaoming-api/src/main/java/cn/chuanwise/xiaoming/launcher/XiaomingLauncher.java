package cn.chuanwise.xiaoming.launcher;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import org.slf4j.Logger;

public interface XiaomingLauncher {
    /**
     * 载入一大堆设置
     * @return
     */
    boolean launch();

    /**
     * 启动小明
     */
    default boolean start() {
        try {
            getXiaomingBot().start();
            setShutdownHook();
            return true;
        } catch (Exception exception) {
            getLogger().error("启动小明时出现异常：", exception);
            return false;
        }
    }

    default void setShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 如果小明此时还没有关闭则关闭
            if (!getXiaomingBot().isStop()) {
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
