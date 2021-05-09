package com.chuanwise.xiaoming.api.url;

import com.chuanwise.xiaoming.api.preserve.Preservable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public interface PictureUrlManager extends Preservable<File>, UrlManager, MiraiCodeManager {
    Pattern PICTURE_URL_PATTERN = Pattern.compile("\\[mirai:image,id=(?<id>.+),url=(?<url>.+)\\]");

    String requireRecordedMessage(String message);

    String requireUrl(String miraiCode);
}
