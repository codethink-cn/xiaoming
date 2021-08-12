package cn.chuanwise.xiaoming.license;

import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.toolkit.preservable.Preservable;

import java.io.File;
import java.util.Map;

public interface LicenseManager extends Preservable<File>, ModuleObject {
    String getLicense();

    void setLicense(String license);

    boolean isAgreed(long code);

    void agree(long code);

    void remove(long code);

    Map<Long, Long> getAgreementRecords();
}
