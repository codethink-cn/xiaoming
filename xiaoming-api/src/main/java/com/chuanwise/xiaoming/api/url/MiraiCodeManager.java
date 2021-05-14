package com.chuanwise.xiaoming.api.url;

import com.chuanwise.xiaoming.api.object.HostObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;

import java.io.File;
import java.util.List;

public interface MiraiCodeManager extends HostObject, Preservable<File> {
    String requireRecordedCatCode(String miraiCode);

    List<String> listCatCodes(String string);
}
