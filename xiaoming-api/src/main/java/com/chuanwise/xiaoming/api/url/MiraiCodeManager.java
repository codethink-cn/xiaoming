package com.chuanwise.xiaoming.api.url;

import com.chuanwise.xiaoming.api.object.HostXiaomingObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;

import java.io.File;
import java.util.List;

public interface MiraiCodeManager extends HostXiaomingObject, Preservable<File> {
    String requireRecordedCatCode(String miraiCode);

    List<String> listCatCodes(String string);
}
