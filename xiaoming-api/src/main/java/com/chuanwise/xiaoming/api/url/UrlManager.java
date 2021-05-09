package com.chuanwise.xiaoming.api.url;

public interface UrlManager {
    boolean contains(String url);

    String requireRecordedUrl(String url);
}
