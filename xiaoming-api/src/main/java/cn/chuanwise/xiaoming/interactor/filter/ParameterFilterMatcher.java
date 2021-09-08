package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.pattern.ParameterPattern;
import cn.chuanwise.utility.MapUtility;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import lombok.Getter;

import java.util.*;

@Getter
public class ParameterFilterMatcher extends FilterMatcher {
    ParameterPattern parameterPattern;

    public ParameterFilterMatcher(String format) {
        this.parameterPattern = new ParameterPattern(format);
    }

    @Override
    public boolean apply(XiaomingUser user, Message message) {
        return MapUtility.nonEmpty(parse(message.serialize()));
    }

    @Override
    public String toUsage() {
        return parameterPattern.getUsage(parameter -> ("[" + parameter + "]"), "  ");
    }

    public boolean matches(String input) {
        return parameterPattern.matches(input);
    }

    public Map<String, String> parse(String input) {
        return parameterPattern.parse(input);
    }
}
