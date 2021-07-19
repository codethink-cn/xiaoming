package cn.chuanwise.xiaoming.api.interactor.detail;

import cn.chuanwise.utility.ArrayUtility;
import cn.chuanwise.xiaoming.api.annotation.Filter;
import cn.chuanwise.xiaoming.api.command.CommandFormat;
import cn.chuanwise.xiaoming.api.configuration.Configuration;
import cn.chuanwise.xiaoming.api.interactor.filter.FilterMatcher;
import cn.chuanwise.xiaoming.api.user.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.beans.Transient;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 交互方法的细节
 */
@Getter
@AllArgsConstructor
public class InteractorMethodDetail {
    /** 具体的交互方法 */
    transient Method method;

    /** 交互判定器 */
    transient FilterMatcher[] filterMatchers;

    CommandFormat commandFormat;

    public InteractorMethodDetail(Method method) {
        this.method = method;
        this.commandFormat = new CommandFormat(method);
        this.filterMatchers = ArrayUtility.copyAs(method.getAnnotationsByType(Filter.class), FilterMatcher.class, FilterMatcher::filterMatcher);
    }

    public InteractorMethodDetail(Method method, CommandFormat format) {
        this.method = method;
        this.commandFormat = format;
        this.filterMatchers = ArrayUtility.copyAs(format.getFormats(), FilterMatcher.class, FilterMatcher::parameter);
    }

    public boolean willInteract(XiaomingUser user) {
        return commandFormat.willInteract(user);
    }

    @Transient
    public Method getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InteractorMethodDetail that = (InteractorMethodDetail) o;
        return method.equals(that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method);
    }
}