package cn.chuanwise.xiaoming.language;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.language.sentence.Sentence;
import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.Data;

import java.util.Objects;
import java.util.Optional;

@Data
public class MultipleLanguageFinder {
    final Plugin plugin;
    final XiaomingBot xiaomingBot;

    public Sentence getSentence(String name) {
        final LanguageManager languageManager = xiaomingBot.getLanguageManager();
        if (Objects.nonNull(plugin)) {
            for (Language language : languageManager.getLanguages(plugin)) {
                final Optional<Sentence> optionalSentence = language.getSentence(name);
                if (optionalSentence.isPresent()) {
                    return optionalSentence.get();
                }
            }
        }

        return languageManager.getSentence(name).orElse(null);
    }
}
