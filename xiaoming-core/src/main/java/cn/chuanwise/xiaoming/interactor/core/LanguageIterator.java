package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.language.Language;
import cn.chuanwise.xiaoming.language.LanguageManager;
import cn.chuanwise.xiaoming.utility.CommandWords;
import cn.chuanwise.xiaoming.interactor.InteractorImpl;

/**
 * 单词指令处理器
 * @author Chuanwise
 */
public class LanguageIterator extends InteractorImpl {
    final LanguageManager language;
    static final String LANGUAGE = "(语言|language)";

    public LanguageIterator(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        language = getXiaomingBot().getLanguageManager();
        setUsageCommandFormat(LANGUAGE + CommandWords.HELP);
    }
}
