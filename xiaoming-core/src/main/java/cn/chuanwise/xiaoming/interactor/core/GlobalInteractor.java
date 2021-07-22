package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.annotation.WhenExternal;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.group.GroupRecordImpl;
import cn.chuanwise.xiaoming.license.LicenseManager;
import cn.chuanwise.xiaoming.group.GroupRecord;
import cn.chuanwise.xiaoming.group.GroupRecordManager;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.utility.CommandWords;
import cn.chuanwise.xiaoming.interactor.InteractorImpl;

import java.util.Objects;

/**
 * 全局指令处理器
 * @author Chuanwise
 */
public class GlobalInteractor extends InteractorImpl {
    public GlobalInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
    }

    @WhenExternal
    @Filter(CommandWords.USE + CommandWords.XIAOMING)
    @Permission("core.use")
    public void onUseXiaoming(XiaomingUser user) {
        final Configuration config = getXiaomingBot().getConfiguration();
        if (config.isEnableLicense()) {
            final LicenseManager licenceManager = getXiaomingBot().getLicenseManager();
            final long qq = user.getCode();
            if (licenceManager.isAgreed(qq)) {
                user.sendMessage("你此前已经同意了小明使用协议");
            } else {
                /*
                user.sendPrivateMessage(getXiaomingBot().getLanguage().loadOrFail(config.getLicenseName()));
                user.sendPrivateMessage("如果你同意上述协议，请告诉我「同意」");

                final String nextInput = user.nextGlobalInput();
                if (Objects.equals(nextInput, "同意")) {
                    user.sendMessage("你已经可以使用小明了，未来记得遵守我们的约定");
                    licenceManager.agree(qq);
                } else {
                    user.sendMessage("如果未来希望使用小明，仍然可以告诉我「使用小明」");
                }
                getXiaomingBot().getFileSaver().readySave(licenceManager);

                 */
            }
        } else {
            user.sendMessage("不需要专门启动小明哦，小明为人民服务 {happy}");
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
                user.sendMessage("已取消使用小明。如果未来希望使用小明，仍然可以告诉我「使用小明」");
                getXiaomingBot().getFileSaver().readySave(licenceManager);
            } else {
                user.sendMessage("此前你并未同意《小明使用须知》");
            }
        } else {
            user.sendMessage("不需要专门取消小明哦，小明为人民服务 {happy}");
        }
    }

    @WhenExternal
    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.ENABLE + CommandWords.XIAOMING)
    @Permission("group.enable")
    public void onEnableXiaoming(GroupXiaomingUser user) {
        final GroupContact contact = user.getContact();
        final GroupRecordManager groupRecordManager = getXiaomingBot().getGroupRecordManager();

        GroupRecord groupRecord = groupRecordManager.forCode(contact.getCode());
        if (Objects.isNull(groupRecord)) {
            groupRecord = new GroupRecordImpl(contact.getCode(), contact.getName());
            groupRecordManager.addGroup(groupRecord);
            getXiaomingBot().getFileSaver().readySave(groupRecordManager);
        }

        if (groupRecord.hasTag(getXiaomingBot().getConfiguration().getEnableGroupTag())) {
            user.sendWarning("本群已经是小明的响应群了哦");
        } else {
            groupRecord.addTag(getXiaomingBot().getConfiguration().getEnableGroupTag());
            user.sendMessage("成功在本群启用小明 (๑•̀ㅂ•́)و✧");
            getXiaomingBot().getFileSaver().readySave(groupRecordManager);
        }
    }

    @WhenExternal
    @Filter(CommandWords.THIS + CommandWords.GROUP + CommandWords.DISABLE + CommandWords.XIAOMING)
    @Permission("group.disable")
    public void onDisableXiaoming(GroupXiaomingUser user) {
        final GroupRecordManager groupRecordManager = getXiaomingBot().getGroupRecordManager();

        GroupRecord groupRecord = user.getGroupRecord();
        if (Objects.isNull(groupRecord)) {
            user.sendMessage("本群还不是小明的响应群哦");
        }

        if (groupRecord.hasTag(getXiaomingBot().getConfiguration().getEnableGroupTag())) {
            groupRecord.removeTag(getXiaomingBot().getConfiguration().getEnableGroupTag());
            user.sendMessage("本群不再是小明的响应群啦。未来希望启动小明输入「本群启动小明」就可以啦。");
        } else {
            user.sendWarning("本群曾是小明的响应群，但是现在还不是哦");
            getXiaomingBot().getFileSaver().readySave(groupRecordManager);
        }
    }
}