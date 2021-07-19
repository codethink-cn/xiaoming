package cn.chuanwise.xiaoming.api.command;

import cn.chuanwise.utility.ArrayUtility;
import cn.chuanwise.utility.StringUtility;
import cn.chuanwise.xiaoming.api.annotation.*;
import cn.chuanwise.xiaoming.api.configuration.Configuration;
import cn.chuanwise.xiaoming.api.user.*;
import cn.chuanwise.xiaoming.api.utility.UsageStringUtility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/***
 * 描述指令的格式。
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandFormat {
    String name;

    String[] formats = new String[0];
    String[] permissions = new String[0];
    String[] usages = new String[0];
    boolean externalUsable = false;
    boolean quietUsable = false;
    boolean nonNext = false;
    String[] requireGroupTags = new String[0];
    String[] requireAccountTags = new String[0];

    /** 从交互方法获得指令格式。如果该方法不是交互方法，抛出异常 */
    public CommandFormat(Method method) {
        // 设置所需权限
        final Filter[] filters = method.getAnnotationsByType(Filter.class);
        if (filters.length == 0) {
            throw new IllegalArgumentException("method: " + method + " is not a interact method.");
        }

        name = method.getName();
        formats = ArrayUtility.copyAs(filters, String.class, Filter::value);
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