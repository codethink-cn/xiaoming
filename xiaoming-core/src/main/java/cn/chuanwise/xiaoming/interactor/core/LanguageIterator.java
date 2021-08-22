package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.language.LanguageManager;
import cn.chuanwise.xiaoming.utility.CommandWords;

/**
 * 单词指令处理器
 * @author Chuanwise
 */
public class LanguageIterator extends SimpleInteractors {
    LanguageManager language;

    @Override
    public void onRegister() {
        language = getXiaomingBot().getLanguageManager();
    }
}
