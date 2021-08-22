package cn.chuanwise.xiaoming.interactor.parser;

import cn.chuanwise.api.Registrable;
import cn.chuanwise.toolkit.value.container.ValueContainer;
import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.Data;

import java.util.function.Function;

@Data
public class InteractorParameterParserHandler<T> {
    final Class<T> parameterClass;
    final InteractorParameterParser<T> parser;
    final Plugin plugin;
    final boolean shared;
}
