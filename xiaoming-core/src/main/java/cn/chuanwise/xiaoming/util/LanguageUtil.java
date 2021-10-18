package cn.chuanwise.xiaoming.util;

import cn.chuanwise.toolkit.preservable.loader.FileLoader;
import cn.chuanwise.util.ResourceUtil;
import cn.chuanwise.util.StaticUtil;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.language.Language;
import cn.chuanwise.xiaoming.language.LanguageImpl;
import cn.chuanwise.xiaoming.language.sentence.Sentence;
import cn.chuanwise.xiaoming.schedule.FileSaver;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class LanguageUtil extends StaticUtil {
    public static Language loadOrCopy(XiaomingBot bot, File languageFile, ClassLoader classLoader, String resourcePath) throws IOException {
        // 直接载入，可能会因为文件不存在之类的失败
        final FileSaver fileSaver = bot.getFileSaver();
        final FileLoader fileLoader = bot.getFileLoader();

        Language savedLanguage = fileLoader.loadOrFail(LanguageImpl.class, languageFile);

        // 如果失败，则覆盖复制
        if (Objects.isNull(savedLanguage)) {
            ResourceUtil.copyResource(classLoader, resourcePath, languageFile, true);
            savedLanguage = fileLoader.loadOrFail(LanguageImpl.class, languageFile);

            return savedLanguage;
        }

        // 对照更新
        boolean modified = false;
        Language defaultLanguage = fileLoader.getDefaultSerializer().deserialize(classLoader.getResourceAsStream(resourcePath), "UTF-8", LanguageImpl.class);
        for (Map.Entry<String, Sentence> entry : defaultLanguage.getSentences().entrySet()) {
            final String key = entry.getKey();
            final Sentence value = entry.getValue();

            // 找到存储的语句，如果存储了，则设置默认值，否则将其添加
            final Optional<Sentence> optionalSentence = savedLanguage.getSentence(key);
            if (optionalSentence.isEmpty()) {
                savedLanguage.addSentence(key, value);
                modified = true;
            } else {
                final Sentence sentence = optionalSentence.get();
                if (!Objects.equals(sentence.getDefaultValue(), value.getDefaultValue())) {
                    sentence.setDefaultValue(value.getDefaultValue());
                    modified = true;
                }
            }
        }

        if (modified) {
            fileSaver.saveOrFail(savedLanguage);
        }

        return savedLanguage;
    }
}
