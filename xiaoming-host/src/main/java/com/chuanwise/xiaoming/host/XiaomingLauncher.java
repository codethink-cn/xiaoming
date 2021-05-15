package com.chuanwise.xiaoming.host;

import com.chuanwise.xiaoming.api.util.PathUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 小明启动器
 * https://github.com/Chuanwise/xiaoming-bot
 * @author Chuanwise
 */
@Slf4j
public class XiaomingLauncher {
    public static void main(String[] args) {
        final File launcherDir = PathUtil.LAUNCHER_DIR;
        if (!launcherDir.isDirectory() && !launcherDir.mkdirs()) {
            log.error("无法创建启动器配置文件夹：" + launcherDir.getAbsolutePath());
            return;
        }

        final XiaomingHost launcher = new XiaomingHost();

        // 设置关闭时的数据保存操作
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 如果小明此时还没有关闭则关闭
            if (!launcher.getXiaomingBot().isStop()) {
                launcher.stop();
            }
        }));

        launcher.launch();
    }
}
