package cn.chuanwise.xiaoming.permission;

import cn.chuanwise.api.ChineseConvertable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Accessible implements ChineseConvertable {
    ACCESSIBLE("有"),
    UNACCESSIBLE("无"),
    UNKNOWN("未知");

    protected final String chinese;

    @Override
    public String toChinese() {
        return chinese;
    }
}
