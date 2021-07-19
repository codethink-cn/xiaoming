package cn.chuanwise.xiaoming.api.license;

import cn.chuanwise.xiaoming.api.object.ModuleObject;
import cn.chuanwise.toolkit.preservable.Preservable;

import java.io.File;

public interface LicenseManager extends Preservable<File>, ModuleObject {
    boolean isAgreed(long qq);

    void agree(long qq);

    void remove(long qq);

    java.util.Map<Long, Long> getAgreements();
}
