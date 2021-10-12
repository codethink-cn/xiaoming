package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.language.LanguageManager;

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
