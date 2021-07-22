package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.language.Language;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.utility.CommandWords;
import cn.chuanwise.xiaoming.interactor.InteractorImpl;

import java.util.*;

/**
 * 单词指令处理器
 * @author Chuanwise
 */
public class LanguageIterator extends InteractorImpl {
    final Language language;
    static final String WORD = "(单词|word|words)";

    public LanguageIterator(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        language = getXiaomingBot().getLanguage();
        setUsageCommandFormat(WORD + CommandWords.HELP);
    }

    @Filter(WORD + " {key}")
    @Permission("emoji.look")
    public void onListEmoji(XiaomingUser user,
                            @FilterParameter("key") String key) {
        final Object object = language.get(key);
        if (Objects.isNull(object)) {
            user.sendMessage("{xiaoming}没有收录任何有关「{key}」{}的提示语");
        }
        else {
            if (object instanceof Collection) {
                user.sendMessage("{xiaoming}收录的「{key}」类提示语有：" + CollectionUtility.toString(((Collection<?>) object), Object::toString));
            } else {
                user.sendMessage("{xiaoming}收录的「{key}」类提示语为：" + object);
            }
        }
    }

    @Filter(WORD + " {key} " + CommandWords.NEW + " {remain}")
    @Permission("emoji.add")
    public void onAddEmoji(XiaomingUser user,
                           @FilterParameter("key") String key,
                           @FilterParameter("remain") String content) {
        if (content.isEmpty()) {
            user.sendError("添加的单词不能为空");
            return;
        }
        final Object object = language.get(key);
        boolean isNewKey = false;
        if (Objects.isNull(object)) {
            language.put(key, content);
            isNewKey = true;
        } else if (object instanceof String) {
            final HashSet<Object> objects = new HashSet<>();
            objects.add(object);
            objects.add(content);
            language.put(key, object);
        } else {
            ((Collection<String>) object).add(content);
        }

        if (isNewKey) {
            user.sendMessage("成功添加了新的提示语「{key}」「{content}」");
        } else {
            user.sendMessage("成功在现有提示语「{key}」中追加了新的「{content}」");
        }

        getXiaomingBot().getFileSaver().readySave(language);
    }
}
