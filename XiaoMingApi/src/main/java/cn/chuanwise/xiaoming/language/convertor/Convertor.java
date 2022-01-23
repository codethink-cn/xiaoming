package cn.chuanwise.xiaoming.language.convertor;

@FunctionalInterface
public interface Convertor<T> {
    String convert(T from);
}
