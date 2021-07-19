package cn.chuanwise.xiaoming.core.license;

import cn.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.license.LicenseManager;
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

    @Transient
    @Override
    public Logger getLog() {
        return log;
    }

    Map<Long, Long> agreements = new HashMap<>();

    public void setAgreements(Map<Long, Long> agreements) {
        this.agreements = agreements;
    }

    @Override
    public boolean isAgreed(long qq) {
        return agreements.containsKey(qq) || getXiaomingBot().getMiraiBot().getId() == qq;
    }

    @Override
    public void agree(long qq) {
        agreements.put(qq, System.currentTimeMillis());
    }

    @Override
    public void remove(long qq) {
        agreements.remove(qq);
    }
}
