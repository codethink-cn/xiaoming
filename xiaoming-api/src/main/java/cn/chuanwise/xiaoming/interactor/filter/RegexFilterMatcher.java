package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.utility.UsageStringUtility;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.regex.Pattern;

@AllArgsConstructor
@Data
public abstract class RegexFilterMatcher extends FilterMatcher {
    Pattern pattern;

    @Override
    public String toUsage() {
        return UsageStringUtility.translateUsageRegex(pattern.pattern());
    }
}
