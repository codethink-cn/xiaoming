package com.chuanwise.xiaoming.host.config;

import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.*;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * 机器人账号密码配置文件
 * @author Chuanwise
 */
@Data
public class LauncherConfig extends JsonFilePreservable {
    /**
     * 机器人账号密码
     */
    BotAccount account = new BotAccount();

    String logFileNamePattern = "yyyy-mm-dd hh:mm:ss";
}
