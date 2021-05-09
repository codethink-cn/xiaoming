package com.chuanwise.xiaoming.core.command.executor;

import com.chuanwise.xiaoming.api.annotation.Command;
import com.chuanwise.xiaoming.api.annotation.CommandParameter;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.word.WordManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 单词指令处理器
 * @author Chuanwise
 */
public class WordCommandExecutor extends CommandExecutorImpl {
    final WordManager wordManager;
    static final String WORD = "(单词|word|words)";

    public WordCommandExecutor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        wordManager = getXiaomingBot().getWordManager();
    }

    @Override
    public String usageStringsPrefix() {
        return WORD;
    }

    @Command(WORD + " {key}")
    @RequirePermission("emoji.look")
    public void onListEmoji(XiaomingUser user,
                            @CommandParameter("key") final String key) {
        final Set<String> set = wordManager.getSet(key);
        if (Objects.isNull(set) || set.isEmpty()) {
            user.sendMessage("小明没有收录任何有关{}的单词哦", key);
        }
        else {
            user.sendMessage("小明收录的{}类单词有：{}", key, set);
        }
    }

    @Command(WORD)
    @RequirePermission("emoji.list")
    public void onListEmoji(XiaomingUser user) {
        final Set<Map.Entry<String, Set<String>>> entries = wordManager.getValues().entrySet();
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

    @Command(WORD + " {key} " + CommandWords.NEW_REGEX + " {remain}")
    @RequirePermission("emoji.add")
    public void onAddEmoji(XiaomingUser user,
                           @CommandParameter("key") final String key,
                           @CommandParameter("remain") final String emoji) {
        if (emoji.isEmpty()) {
            user.sendError("添加的单词不能为空");
            return;
        }
        final Map<String, Set<String>> map = wordManager.getValues();
        Set<String> emojiSet = map.get(key);
        if (Objects.isNull(emojiSet)) {
            emojiSet = new HashSet<>();
            map.put(key, emojiSet);
        }
        emojiSet.add(emoji);
        user.sendMessage("成功添加了{}类型单词：{}", key, emoji);
        getXiaomingBot().getRegularPreserveManager().readySave(wordManager);
    }
}
