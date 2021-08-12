package cn.chuanwise.xiaoming.language;

import lombok.Data;

@Data
public abstract class StringConvertor<T> {
    final Class<T> clazz;

    public StringConvertor(Class<T> clazz) {
        this.clazz = clazz;
    }

    public abstract String convert(T value);
}
