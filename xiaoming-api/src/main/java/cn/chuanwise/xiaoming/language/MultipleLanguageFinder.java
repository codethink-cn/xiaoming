package cn.chuanwise.xiaoming.language;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.language.sentence.Sentence;
import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.Data;

import java.util.Objects;

@Data
public class MultipleLanguageFinder {
    final Plugin plugin;
    final XiaomingBot xiaomingBot;

    public Sentence getSentence(String name) {
        final LanguageManager languageManager = xiaomingBot.getLanguageManager();
        if (Objects.nonNull(plugin)) {
            for (Language language : languageManager.getLanguages(plugin)) {
                final Sentence sentence = language.getSentence(name);
                if (Objects.nonNull(sentence)) {
                    return sentence;
                }
            }
        }

        return languageManager.getSentence(name);
    }
}
