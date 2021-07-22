package cn.chuanwise.xiaoming.interactor;

import cn.chuanwise.utility.ArrayUtility;
import cn.chuanwise.utility.StringUtility;
import cn.chuanwise.xiaoming.annotation.*;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.interactor.filter.FilterMatcher;
import cn.chuanwise.xiaoming.user.*;
import cn.chuanwise.xiaoming.utility.UsageStringUtility;
import lombok.*;

import java.beans.Transient;
import java.lang.reflect.Method;
import java.util.*;

/***
 * 描述指令的格式。
 *
 */
@Data
@NoArgsConstructor
public class InteractorMethodInformation {
    String[] formats = new String[0];
    String[] permissions = new String[0];
    String[] usages = new String[0];
    String[] requireGroupTags = new String[0];
    String[] requireAccountTags = new String[0];

    boolean externalUsable = false;
    boolean quietUsable = false;
    boolean nonNext = false;

    /** 具体的交互方法 */
    transient Method method;

    @Transient
    public Method getMethod() {
        return method;
    }

    /** 交互判定器 */
    transient FilterMatcher[] filterMatchers;

    @Transient
    public FilterMatcher[] getFilterMatchers() {
        return filterMatchers;
    }

    /** 从交互方法获得指令格式。如果该方法不是交互方法，抛出异常 */
    public InteractorMethodInformation(Method method) {
        // 设置所需权限
        final Filter[] filters = method.getAnnotationsByType(Filter.class);
        if (filters.length == 0) {
            throw new IllegalArgumentException("method: " + method + " is not a interact method.");
        }

//        name = method.getName();
        formats = ArrayUtility.copyAs(filters, String.class, Filter::value);
        filterMatchers = ArrayUtility.copyAs(filters, FilterMatcher.class, FilterMatcher::filterMatcher);
        permissions = ArrayUtility.copyAs(method.getAnnotationsByType(Permission.class), String.class, Permission::value);

        final List<String> usageStrings = new ArrayList<>();
        for (Filter filter : filters) {
            if (filter.enableUsage()) {
                usageStrings.add(StringUtility.chooseFirstNonEmptyOrSupplie(null, filter.usage(), UsageStringUtility.translateUsageRegex(filter.value())));
            }
        }
        usages = usageStrings.toArray(new String[0]);

        quietUsable = method.getAnnotationsByType(WhenQuiet.class).length != 0;
        externalUsable = method.getAnnotationsByType(WhenExternal.class).length != 0;

        requireAccountTags = ArrayUtility.copyAs(method.getAnnotationsByType(RequireAccountTag.class), String.class, RequireAccountTag::value);
        requireGroupTags = ArrayUtility.copyAs(method.getAnnotationsByType(RequireGroupTag.class), String.class, RequireGroupTag::value);
    }

    public InteractorMethodInformation(String[] formats, String[] permissions, String[] requireGroupTags, String[] requireAccountTags, boolean externalUsable, boolean quietUsable, boolean nonNext) {
//        this.name = name;
        setFormats(formats);
        this.permissions = permissions;
        this.requireAccountTags = requireAccountTags;
        this.requireGroupTags = requireGroupTags;
        this.externalUsable = externalUsable;
        this.quietUsable = quietUsable;
        this.nonNext = nonNext;
    }

    public void setFormats(String[] formats) {
        this.formats = formats;
        this.filterMatchers = ArrayUtility.copyAs(formats, FilterMatcher.class, FilterMatcher::parameter);
        this.usages = ArrayUtility.copyAs(formats, String.class, UsageStringUtility::translateUsageRegex);
    }

    public boolean willInteract(XiaomingUser user) {
        // 控制台、私聊和临时会话立刻响应
        if (user instanceof ConsoleXiaomingUser) {
            return true;
        }

        // 检查 AccountTag
        for (String tag : requireAccountTags) {
            if (!user.hasTag(tag)) {
                return false;
            }
        }
        if (user instanceof PrivateXiaomingUser ||
                user instanceof MemberXiaomingUser) {
            return true;
        }

        if (!(user instanceof GroupXiaomingUser)) {
            throw new IllegalArgumentException("user is not instance of GroupXiaomingUser, PrivateXiaomingUser, MemberXiaomingUser or ConsoleXiaomingUser.");
        }

        final GroupXiaomingUser groupXiaomingUser = (GroupXiaomingUser) user;
        final Set<String> contactTags = groupXiaomingUser.getContact().getTags();

        // WhenQuiet
        // 在群里、开启了安静模式且本方法不是安静方法
        final Configuration configuration = user.getXiaomingBot().getConfiguration();
        if (!quietUsable &&
                contactTags.contains(configuration.getQuietModeGroupTag()) &&
                !user.hasPermission("quiet.bypass")) {
            return false;
        }

        // WhenExternal
        // 外部方法
        if (!externalUsable &&
                !contactTags.contains(configuration.getEnableGroupTag())) {
            return false;
        }

        // 检查 GroupTag
        for (String tag : requireGroupTags) {
            if (contactTags.contains(tag)) {
                return true;
            }
        }
        return true;
    }
}