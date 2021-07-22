package cn.chuanwise.xiaoming.language;

import cn.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.language.Language;
import lombok.Data;

import java.util.*;

@Data
public class LanguageImpl extends FilePreservableImpl implements Language {
    transient static final Random RANDOM = new Random();
    transient XiaomingBot xiaomingBot;
    Map<String, Object> values = new HashMap<>();
    {
        values.put("happy", new ArrayList<>(Arrays.asList("(๑•̀ㅂ•́)و✧", "ヾ(^▽^*)))", "ヾ(•ω•`)o")));

        values.put("warning", new ArrayList<>(Arrays.asList("(ノへ￣、)", "(っ °Д °;)っ", "(ﾟДﾟ*)ﾉ")));

        values.put("error", new ArrayList<>(Arrays.asList("(〃＞目＜)", "（＞人＜；）", "ヽ(*。>Д<)o゜", "(ﾟДﾟ*)ﾉ", "(ノ｀Д)ノ", "( ´･･)ﾉ(._.`)", "（；´д｀）ゞ")));
    }

    @Override
    public String getStringOrDefault(String key, String onFail) {
        final Object object = get(key);
        String result = key;
        if (object instanceof String) {
            result = ((String) object);
        } else if (object instanceof Collection && !((Collection<?>) object).isEmpty()) {
            try {
                final Collection<String> collection = (Collection<String>) object;
                result = collection.toArray(new Object[0])[RANDOM.nextInt(collection.size())] + "";
            } catch (ClassCastException ignored) {
            }
        } else if (Objects.nonNull(object)) {
            result = object.toString();
        }
        return result;
    }
}
