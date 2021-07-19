package cn.chuanwise.xiaoming.api.interactor.filter;

import cn.chuanwise.xiaoming.api.utility.UsageStringUtility;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.regex.Pattern;

@AllArgsConstructor
@Data
public abstract class RegexFilterMatcher extends FilterMatcher {
    Pattern pattern;

    @Override
    public String toString() {
        return UsageStringUtility.translateUsageRegex(pattern.pattern());
    }
}
