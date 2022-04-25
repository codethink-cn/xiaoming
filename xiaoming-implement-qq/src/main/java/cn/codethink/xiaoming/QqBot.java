package cn.codethink.xiaoming;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.annotation.InternalAPI;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.code.LongCode;
import cn.codethink.xiaoming.configuration.BotConfiguration;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.event.EventForwarder;
import cn.codethink.xiaoming.logger.QqLogger;
import cn.codethink.xiaoming.message.module.MessageModule;
import cn.codethink.xiaoming.message.modules.QqModules;
import cn.codethink.xiaoming.util.Qqs;
import lombok.Data;
import net.mamoe.mirai.Bot;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author Chuanwise
 */
@InternalAPI
@Data
public class QqBot
    extends AbstractBot {
    
    static {
//        Bots.registerDriver(IM.QQ, )
        
        // register message module
        MessageModule.registerModule(new QqModules());
    }
    
    /**
     * Qq 实现 Bot 本体
     */
    protected final Bot qqBot;
    
    /**
     * Bot 自身的序列化
     */
    protected final LongCode code;
    
    /**
     * 会话管理器
     */
    protected QqContactManager contactManager;
    
    /**
     * 账户信息
     */
    private QqProfile profile;
    
    protected QqBot(Bot qqBot) {
        Preconditions.objectNonNull(qqBot, "qq bot");
        this.qqBot = qqBot;
    
        final net.mamoe.mirai.utils.BotConfiguration configuration = qqBot.getConfiguration();
        configuration.noBotLog();
        code = LongCode.valueOf(qqBot.getId());
    }
    
    @Override
    protected void setupBotConfiguration(BotConfiguration botConfiguration) {
        // set working directory
        final net.mamoe.mirai.utils.BotConfiguration configuration = qqBot.getConfiguration();
        configuration.setWorkingDir(botConfiguration.getWorkingDirectory());
        
        // enable device info
        final File deviceInfoFile = botConfiguration.getDeviceInfoFile();
        if (Objects.nonNull(deviceInfoFile)) {
            configuration.fileBasedDeviceInfo(deviceInfoFile.getAbsolutePath());
        }
    
        // control bot log
        if (botConfiguration.isHideImplementBotLog()) {
            configuration.noBotLog();
        } else {
            final QqLogger qqLogger = new QqLogger(this);
            
            net.mamoe.mirai.utils.MiraiLogger.setDefaultLoggerCreator(name -> qqLogger);
            configuration.setBotLoggerSupplier(bot -> qqLogger);
            configuration.setNetworkLoggerSupplier(bot -> qqLogger);
        }
        
        // log protocol
        final net.mamoe.mirai.utils.BotConfiguration.MiraiProtocol protocol = Qqs.toQq(botConfiguration.getProtocol());
        configuration.setProtocol(protocol);
    }
    
    @Override
    public void start0() {
        if (!qqBot.isOnline()) {
            qqBot.login();
        }
        
        // fetch contacts
        contactManager = new QqContactManager(this);
    
        // bot profile
        profile = asFriend().getProfile();
    
        // register event forwarder
        qqBot.getEventChannel().registerListenerHost(new EventForwarder(this));
    }
    
    @Override
    public void stop0() {
        qqBot.close();
        
        contactManager = null;
    }
    
    @Override
    public QqFriend asFriend() {
        return contactManager.getBotAsFriend();
    }
    
    @Override
    public QqStranger asStranger() {
        return contactManager.getBotAsStranger();
    }
    
    @Override
    public Map<Code, QqFriend> getFriends() {
        return Collections.unmodifiableMap(
            contactManager.getFriends()
        );
    }
    
    @Override
    public QqFriend getFriend(Code code) {
        Preconditions.objectNonNull(code, "code");
        return contactManager.getFriend(code);
    }
    
    @Override
    public QqFriend getFriendOrFail(Code code) {
        return (QqFriend) super.getFriendOrFail(code);
    }
    
    @Override
    public Map<Code, QqGroup> getMasses() {
        return contactManager.getGroups();
    }
    
    @Override
    public Map<Code, QqGroup> getGroups() {
        return getMasses();
    }
    
    @Override
    public QqStranger getStranger(Code code) {
        Preconditions.objectNonNull(code, "code");
        return contactManager.getStranger(code);
    }
    
    @Override
    public QqStranger getStrangerOrFail(Code code) {
        return (QqStranger) super.getStrangerOrFail(code);
    }
    
    @Override
    public Map<Code, QqStranger> getStrangers() {
        return contactManager.getStrangers();
    }
    
    @Override
    public QqGroup getMass(Code code) {
        return contactManager.getGroup(code);
    }
    
    @Override
    public QqGroup getGroup(Code code) {
        return (QqGroup) super.getGroup(code);
    }
    
    @Override
    public QqGroup getGroupOrFail(Code code) {
        return (QqGroup) super.getGroupOrFail(code);
    }
    
    @Override
    public IM getIM() {
        return IM.QQ;
    }
    
    @Override
    public String getRemarkName() {
        return qqBot.getNick();
    }
    
    @Override
    public String getAccountName() {
        return qqBot.getNick();
    }
    
    @Override
    public Profile getProfile() {
        return profile;
    }
    
    @Override
    public Code getCode() {
        return code;
    }
    
    @Override
    public String getAvatarUrl() {
        return qqBot.getAvatarUrl();
    }
    
    @Override
    public String getSenderName() {
        return qqBot.getNick();
    }
}
