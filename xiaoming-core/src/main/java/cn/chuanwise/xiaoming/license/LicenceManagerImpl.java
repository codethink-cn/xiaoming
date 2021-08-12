package cn.chuanwise.xiaoming.license;

import cn.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.beans.Transient;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chuanwise
 */
@Slf4j
@Data
public class LicenceManagerImpl extends FilePreservableImpl implements LicenseManager {
    transient XiaomingBot xiaomingBot;

    String license;

    @Transient
    @Override
    public Logger getLogger() {
        return log;
    }

    Map<Long, Long> agreementRecords = new HashMap<>();

    public void setAgreementRecords(Map<Long, Long> agreementRecords) {
        this.agreementRecords = agreementRecords;
    }

    @Override
    public boolean isAgreed(long qq) {
        return agreementRecords.containsKey(qq) || getXiaomingBot().getMiraiBot().getId() == qq;
    }

    @Override
    public void agree(long qq) {
        agreementRecords.put(qq, System.currentTimeMillis());
    }

    @Override
    public void remove(long qq) {
        agreementRecords.remove(qq);
    }
}
