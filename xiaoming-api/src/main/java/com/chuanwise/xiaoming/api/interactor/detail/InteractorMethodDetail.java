package com.chuanwise.xiaoming.api.interactor.detail;

import com.chuanwise.xiaoming.api.annotation.*;
import com.chuanwise.xiaoming.api.interactor.filter.FilterMatcher;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.ArrayUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@AllArgsConstructor
public class InteractorMethodDetail {
    Method method;
    String[] requiredPermissions;
    FilterMatcher[] filterMatchers;
    boolean blocking = false;

    public InteractorMethodDetail(Method method) {
        this.method = method;

        // 设置所需权限
        requiredPermissions = ArrayUtils.copyAs(method.getAnnotationsByType(RequirePermission.class), String.class, RequirePermission::value);

        // 设置判定器
        filterMatchers = ArrayUtils.copyAs(method.getAnnotationsByType(Filter.class), FilterMatcher.class, FilterMatcher::filterMatcher);
    }

    /**
     * 判断本方法是否会与某用户交互
     * @param user 该用户
     * @return 使用的过滤器
     */
    public boolean willInteract(XiaomingUser user) {
        // 无条件服务控制台使用者
        if (user == user.getXiaomingBot().getConsoleXiaomingUser()) {
            return true;
        }

        final GroupInteractor[] groupInteractors = method.getAnnotationsByType(GroupInteractor.class);
        final TempInteractor[] tempInteractors = method.getAnnotationsByType(TempInteractor.class);
        final PrivateInteractor[] privateInteractors = method.getAnnotationsByType(PrivateInteractor.class);

        final boolean hasGroupRestrict = groupInteractors.length > 0;
        final boolean hasTempRestrict = tempInteractors.length > 0;
        final boolean hasPrivateRestrict = privateInteractors.length > 0;

        // 三个限制都没有就允许
        if (!hasGroupRestrict && !hasTempRestrict && !hasPrivateRestrict) {
            return true;
        }

        // 群交互验证
        boolean groupVerify = false;
        if (hasGroupRestrict) {
            if (user.inGroup()) {
                final GroupInteractor annotation = groupInteractors[0];
                final long group = annotation.value();
                final long qq = annotation.qq();
                groupVerify = (group == 0 || user.getGroup().getId() == group) && (qq == 0 || user.getQQ() == qq);
            } else {
                groupVerify = false;
            }
        }

        // 临时会话验证
        boolean tempVerify = false;
        if (hasTempRestrict) {
            if (user.inTemp()) {
                final TempInteractor annotation = tempInteractors[0];
                final long group = annotation.value();
                final long qq = annotation.qq();
                tempVerify = (group == 0 || user.getGroup().getId() == group) && (qq == 0 || user.getQQ() == qq);
            } else {
                tempVerify = false;
            }
        }

        // 私聊会话验证
        boolean privateVerify = false;
        if (hasPrivateRestrict) {
            if (user.inPrivate()) {
                final PrivateInteractor annotation = privateInteractors[0];
                final long qq = annotation.value();
                privateVerify = qq == 0 || user.getQQ() == qq;
            } else {
                privateVerify = false;
            }
        }

        return groupVerify || privateVerify || tempVerify;
    }

    public FilterMatcher getMatchableFilter(XiaomingUser user) {
        for (FilterMatcher matcher : filterMatchers) {
            if (matcher.apply(user)) {
                return matcher;
            }
        }
        return null;
    }

    public Set<String> getUsages() {
        Set<String> result = new HashSet<>();
        for (FilterMatcher matcher : filterMatchers) {
            result.add(matcher.toString());
        }
        return result;
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