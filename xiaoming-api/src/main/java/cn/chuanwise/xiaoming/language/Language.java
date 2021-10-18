package cn.chuanwise.xiaoming.language;

import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.util.MapUtil;
import cn.chuanwise.xiaoming.language.sentence.Sentence;
import cn.chuanwise.xiaoming.object.PluginObject;

import java.io.File;
import java.util.Map;
import java.util.Optional;

/**
 * 小明的提示文本管理器
 * @author Chuanwise
 */
public interface Language extends Preservable, PluginObject {
    Map<String, Sentence> getSentences();

    default Optional<Sentence> getSentence(String identifier) {
        return MapUtil.get(getSentences(), identifier).toOptional();
    }

    default String getSentenceValue(String identifier) {
        return getSentence(identifier).map(Sentence::getValue).orElse(identifier);
    }

    default void addSentence(String identifier, Sentence sentence) {
        getSentences().put(identifier, sentence);
    }

    default void addSentence(String identifier, String defaultValue, String... customValues) {
        addSentence(identifier, new Sentence(defaultValue, customValues));
    }
}