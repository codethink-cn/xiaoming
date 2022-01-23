package cn.chuanwise.xiaoming.object;

public interface FormatableObject extends XiaomingObject {
    String format(String format, Object... contexts);
}
