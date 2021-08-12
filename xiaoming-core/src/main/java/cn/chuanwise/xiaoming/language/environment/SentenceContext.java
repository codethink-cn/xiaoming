package cn.chuanwise.xiaoming.language.environment;

import lombok.Data;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
public class SentenceContext {
    final List<String> parameterNames;
    final Object[] values;

    @Override
    public String toString() {
        final int maxIndex = Math.max(parameterNames.size(), values.length);
        if (maxIndex == 0) {
            return "空上下文";
        } else {
            final StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < maxIndex; i++) {
                final String name;
                if (i >= parameterNames.size()) {
                    name = "undefined";
                } else {
                    name = parameterNames.get(i);
                }

                final String value;
                if (i >= values.length) {
                    value = "unknown";
                } else {
                    value = Objects.toString(values[i]);
                }

                stringBuffer.append((stringBuffer.length() > 0 ? "；" : "") + name + " = " + value);
            }
            return "上下文（" + stringBuffer + "）";
        }
    }
}
