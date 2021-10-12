package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.annotation.WhenExternal;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.license.LicenseManager;
import cn.chuanwise.xiaoming.group.GroupRecord;
import cn.chuanwise.xiaoming.group.GroupRecordManager;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;

import java.util.Objects;

/**
 * 全局指令处理器
 * @author Chuanwise
 */
public class GlobalInteractors extends SimpleInteractors {
    @WhenExternal
    @Filter(CommandWords.USE + CommandWords.XIAOMING)
    @Permission("core.use")
    public void onUseXiaoming(XiaomingUser user) {
        final Configuration config = getXiaomingBot().getConfiguration();
        if (config.isEnableLicense()) {
            final LicenseManager licenceManager = getXiaomingBot().getLicenseManager();
            final long qq = user.getCode();
            if (licenceManager.isAgreed(qq)) {
                user.sendMessage("{lang.licenseAlreadyAgreed}");
            } else {
                user.sendPrivateMessage(licenceManager.getLicense());
                user.sendPrivateMessage("{lang.enterAgreeIfYouAgree}");

                if (Objects.equals(user.nextMessageOrExit().serialize(), "同意")) {
                    user.sendMessage("{lang.licenseAgreed}");
                    licenceManager.agree(qq);
                } else {
                    user.sendMessage("{lang.licenseDisgreed}");
                }
                getXiaomingBot().getFileSaver().readyToSave(licenceManager);
            }
        } else {
            user.sendMessage("{lang.licenseNotNeedToAgree}");
        }
    }

    @WhenExternal
    @Filter(CommandWords.CANCEL + CommandWords.USE + CommandWords.XIAOMING)
    @Permission("disable")
    public void onCancelUseXiaoming(XiaomingUser user) {
        final Configuration config = getXiaomingBot().getConfiguration();
        if (config.isEnableLicense()) {
            final LicenseManager licenceManager = getXiaomingBot().getLicenseManager();
            final long qq = user.getCode();
            if (licenceManager.isAgreed(qq)) {
                licenceManager.remove(qq);
                user.sendMessage("{lang.licenseCancelled}");
                getXiaomingBot().getFileSaver().readyToSave(licenceManager);
            } else {
                user.sendMessage("{lang.youHadNotAgreeLicense}");
            }
        } else {
            user.sendMessage("{lang.licenseNotNeedToAgree}");
        }
    }

    @WhenExternal
    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.ENABLE + CommandWords.XIAOMING)
    @Permission("group.enable")
    public void onEnableXiaoming(GroupXiaomingUser user) {
        final GroupRecordManager groupRecordManager = getXiaomingBot().getGroupRecordManager();
        final GroupRecord groupRecord = user.getGroupRecord();

        if (groupRecord.hasTag(getXiaomingBot().getConfiguration().getEnableGroupTag())) {
            user.sendWarning("{lang.xiaomingAlreadyEnabledInThisGroup}");
        } else {
            groupRecord.addTag(getXiaomingBot().getConfiguration().getEnableGroupTag());
            user.sendMessage("{lang.xiaomingEnabledInThisGroup}");
            getXiaomingBot().getFileSaver().readyToSave(groupRecordManager);
        }
    }

    @WhenExternal
    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.DISABLE + CommandWords.XIAOMING)
    @Permission("group.disable")
    public void onDisableXiaoming(GroupXiaomingUser user) {
        final GroupRecordManager groupRecordManager = getXiaomingBot().getGroupRecordManager();
        final GroupRecord groupRecord = user.getGroupRecord();

        if (groupRecord.hasTag(getXiaomingBot().getConfiguration().getEnableGroupTag())) {
            groupRecord.removeTag(getXiaomingBot().getConfiguration().getEnableGroupTag());
            user.sendMessage("{lang.xiaomingDisabledInThisGroup}");
        } else {
            user.sendWarning("{lang.xiaomingHadNotEnableInThisGroup}");
            getXiaomingBot().getFileSaver().readyToSave(groupRecordManager);
        }
    }
}