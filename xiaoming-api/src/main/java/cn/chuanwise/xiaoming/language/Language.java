package cn.chuanwise.xiaoming.language;

import cn.chuanwise.util.FunctionalUtil;
import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.xiaoming.language.sentence.Sentence;
import cn.chuanwise.xiaoming.object.PluginObject;

import java.io.File;
import java.util.Map;
import java.util.Objects;

/**
 * 小明的提示文本管理器
 * @author Chuanwise
 */
public interface Language extends Preservable<File>, PluginObject {
    Map<String, Sentence> getSentences();

    default boolean containsSentence(String identifier) {
        return Objects.nonNull(getSentence(identifier));
    }

    default Sentence getSentence(String identifier) {
        return getSentenceOrDefault(identifier, null);
    }

    default Sentence getSentenceOrDefault(String identifier, Sentence defaultValue) {
        return getSentences().getOrDefault(identifier, defaultValue);
    }

    default String getSentenceValueOrDefault(String identifier, String defaultValue) {
        return FunctionalUtil.runIfArgumentNonNullOrDefault(Sentence::getValue, getSentence(identifier), defaultValue);
    }

    default String getSentenceValue(String identifier) {
        return getSentenceValueOrDefault(identifier, identifier);
    }

    default void addSentence(String identifier, Sentence sentence) {
        getSentences().put(identifier, sentence);
    }

    default void addSentence(String identifier, String defaultValue, String... customValues) {
        addSentence(identifier, new Sentence(defaultValue, customValues));
    }
}