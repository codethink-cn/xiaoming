package cn.chuanwise.xiaoming.interactor.parser;

import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.Data;

@Data
public class InteractorParameterParserHandler<T> {
    final Class<T> parameterClass;
    final InteractorParameterParser<T> parser;
    final Plugin plugin;
    final boolean shared;
}
