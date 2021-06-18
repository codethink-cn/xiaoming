package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.Require;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.language.Language;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CollectionUtils;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;

import java.util.*;

/**
 * 单词指令处理器
 * @author Chuanwise
 */
public class LanguageCommandIterator extends CommandInteractorImpl {
    final Language language;
    static final String WORD = "(单词|word|words)";

    public LanguageCommandIterator(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        language = getXiaomingBot().getLanguage();
        enableUsageCommand(WORD);
    }

    @Filter(WORD + " {key}")
    @Require("emoji.look")
    public void onListEmoji(XiaomingUser user,
                            @FilterParameter("key") String key) {
        final Object object = language.get(key);
        if (Objects.isNull(object)) {
            user.sendMessage("{xiaoming}没有收录任何有关「{key}」{}的提示语");
        }
        else {
            if (object instanceof Collection) {
                user.sendMessage("{xiaoming}收录的「{key}」类提示语有：" + CollectionUtils.getSummary(((Collection<?>) object), Object::toString));
            } else {
                user.sendMessage("{xiaoming}收录的「{key}」类提示语为：" + object);
            }
        }
    }

    @Filter(WORD + " {key} " + CommandWords.NEW + " {remain}")
    @Require("emoji.add")
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

        getXiaomingBot().getScheduler().readySave(language);
    }
}
