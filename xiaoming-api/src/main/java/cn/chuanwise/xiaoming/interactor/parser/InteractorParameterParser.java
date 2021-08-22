package cn.chuanwise.xiaoming.interactor.parser;

import cn.chuanwise.toolkit.value.container.ValueContainer;
import cn.chuanwise.xiaoming.user.XiaomingUser;

import java.util.Optional;

/** 智能参数解析器 */
@FunctionalInterface
public interface InteractorParameterParser<T> {
    /** 自定义智能参数解析器 */
    ValueContainer<T> parse(InteractorParameterContext<T> context);
}