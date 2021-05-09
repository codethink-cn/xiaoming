package com.chuanwise.xiaoming.api.util;

import lombok.Getter;

import java.util.Objects;

public class StringUtil {
    @Getter
    private static final StringUtil INSTANCE = new StringUtil();

    public boolean isEmpty(String string) {
        return Objects.isNull(string) || string.isEmpty();
    }

    public String getSpaceString(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(' ');
        }
        return builder.toString();
    }
}
