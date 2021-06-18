package com.chuanwise.xiaoming.host.configuration;

import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.*;
import net.mamoe.mirai.utils.BotConfiguration;

/**
 * 机器人账号密码配置文件
 * @author Chuanwise
 */
@Data
public class LauncherConfiguration extends JsonFilePreservable {
    /**
     * 机器人账号密码
     */
    BotAccount account = new BotAccount();

    BotConfiguration.MiraiProtocol protocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE;

    boolean autoReconnectOnForceOffline = false;

    String logFileNamePattern = "yyyy-mm-dd hh:mm:ss";
}
