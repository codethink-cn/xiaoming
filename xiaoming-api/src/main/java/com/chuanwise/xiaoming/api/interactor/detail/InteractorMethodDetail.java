package com.chuanwise.xiaoming.api.interactor.detail;

import com.chuanwise.xiaoming.api.annotation.*;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.interactor.filter.FilterMatcher;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.ArrayUtils;
import com.chuanwise.xiaoming.api.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.*;

@Getter
@AllArgsConstructor
public class InteractorMethodDetail {
    Method method;
    String[] requiredPermissions;
    FilterMatcher[] filterMatchers;
    String[] usageStrings;
    boolean nonNext = false;
    boolean quietUsable, externalUsable;

    public InteractorMethodDetail(Method method) {
        this.method = method;

        // 设置所需权限
        requiredPermissions = ArrayUtils.copyAs(method.getAnnotationsByType(Require.class), String.class, Require::value);

        // 设置判定器
        final Filter[] filters = method.getAnnotationsByType(Filter.class);
        final List<String> usageStrings = new ArrayList<>();
        filterMatchers = ArrayUtils.copyAs(filters, FilterMatcher.class, filter -> {
            String usage = filter.usage();
            if (StringUtils.isEmpty(usage)) {
                usage = filter.toString();
            }
            usageStrings.add(usage);
            return FilterMatcher.filterMatcher(filter);
        });
        this.usageStrings = usageStrings.toArray(new String[0]);

        quietUsable = method.getAnnotationsByType(WhenQuiet.class).length != 0;
        externalUsable = method.getAnnotationsByType(WhenExternal.class).length != 0;
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
        if (user instanceof GroupXiaomingUser) {
            final long groupCode = ((GroupXiaomingUser) user).getGroupCode();

            // 在群里、开启了安静模式且本方法不是安静方法
            if (!quietUsable &&
                    user.getXiaomingBot().getResponseGroupManager().hasTag(groupCode, "quiet") &&
                    !user.hasPermission("quiet.bypass")) {
                return false;
            }

            // 外部方法
            if (!externalUsable && !user.getXiaomingBot().getResponseGroupManager().hasTag(groupCode, "enable")) {
                return false;
            }
        }
        return true;
    }

    public FilterMatcher getMatchableFilter(XiaomingUser user, Message message) {
        for (FilterMatcher matcher : filterMatchers) {
            if (matcher.apply(user, message)) {
                return matcher;
            }
        }
        return null;
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