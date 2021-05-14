package com.chuanwise.xiaoming.core.word;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.word.WordManager;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
public class WordManagerImpl extends JsonFilePreservable implements WordManager {
    transient static final Random RANDOM = new Random();

    Map<String, Set<String>> values = new HashMap<>();
    transient XiaomingBot xiaomingBot;

    public WordManagerImpl(XiaomingBot xiaomingBot) {
        this.xiaomingBot = xiaomingBot;
    }

    public WordManagerImpl() {}

    @Override
    public Map<String, Set<String>> getValues() {
        return values;
    }

    @Override
    public Set<String> getSet(String key) {
        return values.get(key);
    }

    @Override
    public String get(String key) {
        final Set<String> strings = getSet(key);
        if (Objects.isNull(strings)) {
            return key;
        } else {
            return strings.toArray(new String[0])[RANDOM.nextInt(strings.size())];
        }
    }
}
