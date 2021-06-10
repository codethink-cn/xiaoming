package com.chuanwise.xiaoming.api.license;

import com.chuanwise.xiaoming.api.object.ModuleObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;

import java.io.File;

public interface LicenseManager extends Preservable<File>, ModuleObject {
    boolean isAgreed(long qq);

    void agree(long qq);

    void remove(long qq);

    java.util.Map<Long, Long> getAgreements();
}
