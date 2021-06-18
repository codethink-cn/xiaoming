package com.chuanwise.xiaoming.api.interactor.filter;

import com.chuanwise.xiaoming.api.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@Data
public abstract class RegexFilterMatcher extends FilterMatcher {
    Pattern pattern;

    @Override
    public String toString() {
        return StringUtils.translateUsageRegex(pattern.pattern());
    }
}
