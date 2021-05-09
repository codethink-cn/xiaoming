package com.chuanwise.xiaoming.core.url;

import com.chuanwise.xiaoming.api.url.UrlManager;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

@Data
public abstract class UrlManagerImpl extends JsonFilePreservable implements UrlManager {
    Set<String> urls = new HashSet<>();

    @Override
    public boolean contains(String url) {
        return urls.contains(url);
    }

    @Override
    public String requireRecordedUrl(String url) {
        urls.add(url);
        return url;
    }
}