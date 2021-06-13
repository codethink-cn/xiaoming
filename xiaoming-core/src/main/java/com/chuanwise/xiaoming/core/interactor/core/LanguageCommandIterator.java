package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.language.Language;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;

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
/*
    @Filter(WORD + " {key}")
    @Require("emoji.look")
    public void onListEmoji(XiaomingUser user,
                            @FilterParameter("key") final String key) {
        final Set<String> set = language.getSet(key);
        if (Objects.isNull(set) || set.isEmpty()) {
            user.sendMessage("小明没有收录任何有关{}的单词哦", key);
        }
        else {
            user.sendMessage("小明收录的{}类单词有：{}", key, set);
        }
    }

    @Filter(WORD)
    @Require("emoji.list")
    public void onListEmoji(XiaomingUser user) {
        final Set<Map.Entry<String, Set<String>>> entries = language.getValues().entrySet();
        if (entries.isEmpty()) {
            user.sendMessage("小明没有收录任何单词哦");
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("小明收录了").append(entries.size()).append("种单词：");
        for (Map.Entry<String, Set<String>> entry : entries) {
            builder.append("\n").append(entry.getKey()).append("：").append(entry.getValue());
        }
        user.sendMessage(builder.toString());
    }

    @Filter(WORD + " {key} " + CommandWords.NEW + " {remain}")
    @Require("emoji.add")
    public void onAddEmoji(XiaomingUser user,
                           @FilterParameter("key") final String key,
                           @FilterParameter("remain") final String emoji) {
        if (emoji.isEmpty()) {
            user.sendError("添加的单词不能为空");
            return;
        }
        final Map<String, Set<String>> map = language.getValues();
        Set<String> emojiSet = map.get(key);
        if (Objects.isNull(emojiSet)) {
            emojiSet = new HashSet<>();
            map.put(key, emojiSet);
        }
        emojiSet.add(emoji);
        user.sendMessage("成功添加了{}类型单词：{}", key, emoji);
        getXiaomingBot().getFileSaver().readySave(language);
    }
    */
}
