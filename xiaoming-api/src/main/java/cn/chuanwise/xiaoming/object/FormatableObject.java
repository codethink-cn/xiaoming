package cn.chuanwise.xiaoming.object;

import cn.chuanwise.xiaoming.language.sentence.Sentence;

public interface FormatableObject extends XiaomingObject {
    String format(String format, Object... contexts);

    String format(Sentence sentence, Object... contexts);
}
