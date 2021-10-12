package cn.chuanwise.xiaoming.interactor.parser;

import cn.chuanwise.toolkit.container.Container;

/** 智能参数解析器 */
@FunctionalInterface
public interface InteractorParameterParser<T> {
    /** 自定义智能参数解析器 */
    Container<T> parse(InteractorParameterContext<T> context);
}