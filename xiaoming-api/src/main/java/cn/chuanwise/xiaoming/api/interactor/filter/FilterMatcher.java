package cn.chuanwise.xiaoming.api.interactor.filter;

import cn.chuanwise.xiaoming.api.annotation.Filter;
import cn.chuanwise.xiaoming.api.annotation.FilterPattern;
import cn.chuanwise.xiaoming.api.contact.message.Message;
import cn.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.regex.Pattern;

/**
 * 指令处理方法的格式
 * @author Chuanwise
 */
public abstract class FilterMatcher {
    protected FilterMatcher() {}

    public static FilterMatcher equals(String format) {
        return new EqualsFiliterMatcher(format);
    }

    public static FilterMatcher match(String format) {
        return new MatchFilterMatcher(Pattern.compile(format));
    }

    public static FilterMatcher endsRegex(String format) {
        return new EndsRegexFilterMatcher(Pattern.compile(format));
    }

    public static FilterMatcher startsRegex(String format) {
        return new StartsRegexFilterMatcher(Pattern.compile(format));
    }

    public static FilterMatcher endsWith(String format) {
        return new EndsWithFilterMatcher(format);
    }

    public static FilterMatcher startsWith(String format) {
        return new StartsWithFilterMatcher(format);
    }

    public static FilterMatcher equalsIgnoreCase(String format) {
        return new EqualsIgnoreCaseFilterMatcher(format);
    }

    public static FilterMatcher parameter(String format) {
        return new ParameterFilterMatcher(format);
    }

    /**
     * 验证当前用户的输入是否合法
     * @param user 当前用户
     * @return 输入是否合法
     */
    public abstract boolean apply(XiaomingUser user, Message message);

    /**
     * 生成一个过滤器
     * @param format 过滤器格式
     * @param pattern 过滤方法
     * @return 过滤结果
     */
    public static FilterMatcher filterMatcher(String format, FilterPattern pattern) {
        switch (pattern){
            case MATCHES:
                return match(format);
            case EQUALS:
                return equals(format);
            case ENDS_WITH:
                return endsWith(format);
            case ENDS_REGEX:
                return endsRegex(format);
            case STARTS_WITH:
                return startsWith(format);
            case STARTS_REGEX:
                return startsRegex(format);
            case PARAMETER:
                return parameter(format);
            case EQUALS_IGNORE_CASE:
                return equalsIgnoreCase(format);
            default:
                throw new XiaomingRuntimeException("illegal filter pattern: " + pattern);
        }
    }

    public static FilterMatcher filterMatcher(Filter filter) {
        return filterMatcher(filter.value(), filter.pattern());
    }

    @Override
    public abstract String toString();
}
