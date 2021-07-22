package cn.chuanwise.xiaoming.object;

import cn.chuanwise.xiaoming.bot.XiaomingBot;

import java.beans.Transient;

/**
 * 小明对象
 * 目前只有便捷地获得小明本体引用的功能
 * @author Chuanwise
 */
public interface XiaomingObject {
    /**
     * 获取小明本体引用
     * @return 小明本体
     */
    @Transient
    XiaomingBot getXiaomingBot();

    void setXiaomingBot(XiaomingBot xiaomingBot);
}
