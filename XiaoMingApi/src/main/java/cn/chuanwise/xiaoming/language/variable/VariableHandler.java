package cn.chuanwise.xiaoming.language.variable;

import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.Data;

@Data
public class VariableHandler<T> {
    final String name;
    final VariableGetter<T> getter;
    final Plugin plugin;
}
