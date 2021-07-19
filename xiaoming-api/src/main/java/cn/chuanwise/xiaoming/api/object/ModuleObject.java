package cn.chuanwise.xiaoming.api.object;

import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import org.slf4j.Logger;

/**
 * 小明本体对象
 */
public interface ModuleObject extends XiaomingObject {
    /**
     * 获取当前对象的日志
     * @return 日志对象
     */
    Logger getLog();

    default void flushBotReference(XiaomingBot xiaomingBot) {}
}
