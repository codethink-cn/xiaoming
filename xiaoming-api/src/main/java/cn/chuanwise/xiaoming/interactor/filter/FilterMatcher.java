package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.exception.UnsupportedVersionException;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterPattern;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.exception.XiaomingRuntimeException;
import cn.chuanwise.xiaoming.user.XiaomingUser;

import java.util.regex.Pattern;

/**
 * 指令处理方法的格式
 * @author Chuanwise
 */
public abstract class FilterMatcher {
    protected FilterMatcher() {}

    public static FilterMatcher equals(String format) {
        return new EqualFiliterMatcher(format);
    }

    public static FilterMatcher match(String format) {
        return new MatchFilterMatcher(Pattern.compile(format));
    }

    public static FilterMatcher endMatch(String format) {
        return new EndMatchFilterMatcher(Pattern.compile(format));
    }

    public static FilterMatcher startMatch(String format) {
        return new StartMatchFilterMatcher(Pattern.compile(format));
    }

    public static FilterMatcher containEqual(String string) {
        return new ContainEqualFilterMatcher(string);
    }

    public static FilterMatcher endEqual(String format) {
        return new EndEqualFilterMatcher(format);
    }

    public static FilterMatcher startEqual(String format) {
        return new StartEqualFilterMatcher(format);
    }

    public static FilterMatcher equalsIgnoreCase(String format) {
        return new EqualIgnoreCaseFilterMatcher(format);
    }

    public static FilterMatcher containMatch(String format) {
        return new ContainMatchFilterMatcher(Pattern.compile(format));
    }

    public static FilterMatcher parameter(String format) {
        return new ParameterFilterMatcher(format);
    }

    /**
     * 验证当前用户的输入是否合法
     * @param user 当前用户
     * @return 输入是否合法
     */
    public abstract <M extends Message> boolean apply(XiaomingUser<?, M, ?> user, M message);

    /**
     * 生成一个过滤器
     * @param format 过滤器格式
     * @param pattern 过滤方法
     * @return 过滤结果
     */
    public static FilterMatcher filterMatcher(String format, FilterPattern pattern) {
        switch (pattern){
            case MATCH:
                return match(format);
            case EQUAL:
                return equals(format);
            case CONTAIN_EQUAL:
                return containEqual(format);
            case CONTAIN_MATCH:
                return containMatch(format);
            case END_EQUAL:
                return endEqual(format);
            case END_MATCH:
                return endMatch(format);
            case START_EQUAL:
                return startEqual(format);
            case START_MATCH:
                return startMatch(format);
            case PARAMETER:
                return parameter(format);
            case EQUAL_IGNORE_CASE:
                return equalsIgnoreCase(format);
            default:
                throw new UnsupportedVersionException("illegal filter matcher type: " + pattern);
        }
    }

    public static FilterMatcher filterMatcher(Filter filter) {
        return filterMatcher(filter.value(), filter.pattern());
    }

    public abstract String toUsage();
}
