package cn.codethink.xiaoming;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.annotation.InternalAPI;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.code.LongCode;
import cn.codethink.xiaoming.configuration.BotConfiguration;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.event.EventForwarder;
import cn.codethink.xiaoming.logger.MiraiLogger;
import cn.codethink.xiaoming.message.MiraiMessageParsers;
import cn.codethink.xiaoming.message.parser.MessageParsers;
import cn.codethink.xiaoming.protocol.MiraiProtocol;
import cn.codethink.xiaoming.util.Codes;
import lombok.Data;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.StrangerRelationChangeEvent;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Chuanwise
 */
@InternalAPI
@Data
public class MiraiBot
    extends AbstractBot {
    
    static {
//        Bots.registerDriver(IM.QQ, )
    
        // register message parsers
        MessageParsers.registerParsers(new MiraiMessageParsers());
    }
    
    /**
     * Mirai 实现 Bot 本体
     */
    protected final Bot miraiBot;
    
    /**
     * Bot 自身的编码
     */
    protected final LongCode code;
    
    /**
     * 会话管理器
     */
    protected MiraiContactManager contactManager;
    
    /**
     * 账户信息
     */
    private MiraiProfile profile;
    
    protected MiraiBot(Bot miraiBot) {
        Preconditions.objectNonNull(miraiBot, "mirai bot");
        this.miraiBot = miraiBot;
    
        final net.mamoe.mirai.utils.BotConfiguration configuration = miraiBot.getConfiguration();
        configuration.noBotLog();
        code = LongCode.valueOf(miraiBot.getId());
    }
    
    @Override
    protected void setupBotConfiguration(BotConfiguration botConfiguration) {
        // set working directory
        final net.mamoe.mirai.utils.BotConfiguration configuration = miraiBot.getConfiguration();
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
            final MiraiLogger miraiLogger = new MiraiLogger(this);
            
            net.mamoe.mirai.utils.MiraiLogger.setDefaultLoggerCreator(name -> miraiLogger);
            configuration.setBotLoggerSupplier(bot -> miraiLogger);
            configuration.setNetworkLoggerSupplier(bot -> miraiLogger);
        }
        
        // log protocol
        final net.mamoe.mirai.utils.BotConfiguration.MiraiProtocol protocol = MiraiProtocol.toMiraiProtocol(botConfiguration.getProtocol());
        configuration.setProtocol(protocol);
    }
    
    @Override
    public void start0() {
        if (!miraiBot.isOnline()) {
            miraiBot.login();
        }
        
        // register event forwarder
        miraiBot.getEventChannel().registerListenerHost(new EventForwarder(this));
    
        // fetch contacts
        contactManager = new MiraiContactManager(this);
    
        profile = asFriend().getProfile();
    }
    
    @Override
    public void stop0() {
        miraiBot.close();
        
        contactManager = null;
    }
    
    @Override
    public MiraiFriend asFriend() {
        return contactManager.getBotAsFriend();
    }
    
    @Override
    public MiraiStranger asStranger() {
        return contactManager.getBotAsStranger();
    }
    
    @Override
    public Map<Code, MiraiFriend> getFriends() {
        return Collections.unmodifiableMap(
            contactManager.getFriends()
        );
    }
    
    @Override
    public MiraiFriend getFriend(Code code) {
        Preconditions.objectNonNull(code, "code");
        return contactManager.getFriend(code);
    }
    
    @Override
    public MiraiFriend getFriendOrFail(Code code) {
        return (MiraiFriend) super.getFriendOrFail(code);
    }
    
    @Override
    public Map<Code, MiraiGroup> getMasses() {
        return contactManager.getGroups();
    }
    
    @Override
    public Map<Code, MiraiGroup> getGroups() {
        return getMasses();
    }
    
    @Override
    public MiraiStranger getStranger(Code code) {
        Preconditions.objectNonNull(code, "code");
        return contactManager.getStranger(code);
    }
    
    @Override
    public MiraiStranger getStrangerOrFail(Code code) {
        return (MiraiStranger) super.getStrangerOrFail(code);
    }
    
    @Override
    public Map<Code, MiraiStranger> getStrangers() {
        return contactManager.getStrangers();
    }
    
    @Override
    public MiraiGroup getMass(Code code) {
        return contactManager.getGroup(code);
    }
    
    @Override
    public MiraiGroup getGroup(Code code) {
        return (MiraiGroup) super.getGroup(code);
    }
    
    @Override
    public MiraiGroup getGroupOrFail(Code code) {
        return (MiraiGroup) super.getGroupOrFail(code);
    }
    
    @Override
    public IM getIM() {
        return IM.QQ;
    }
    
    @Override
    public String getRemarkName() {
        return miraiBot.getNick();
    }
    
    @Override
    public String getAccountName() {
        return miraiBot.getNick();
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
        return miraiBot.getAvatarUrl();
    }
    
    @Override
    public String getSenderName() {
        return miraiBot.getNick();
    }
}
