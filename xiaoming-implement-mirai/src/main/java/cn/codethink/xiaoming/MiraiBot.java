package cn.codethink.xiaoming;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.annotation.InternalAPI;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.code.LongCode;
import cn.codethink.xiaoming.configuration.BotConfiguration;
import cn.codethink.xiaoming.contact.Friend;
import cn.codethink.xiaoming.contact.MiraiFriend;
import cn.codethink.xiaoming.contact.MiraiGroup;
import cn.codethink.xiaoming.contact.Scope;
import cn.codethink.xiaoming.event.EventForwarder;
import cn.codethink.xiaoming.logger.MiraiLogger;
import cn.codethink.xiaoming.util.Codes;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;
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
//        Bots.registerDriver(InstantMessenger.QQ, )
    }
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    protected final boolean adapter;
    
    protected final Bot miraiBot;
    
    protected MiraiBot(Bot miraiBot, boolean adapter) {
        Preconditions.namedArgumentNonNull(miraiBot, "mirai bot");
        
        this.miraiBot = miraiBot;
        this.adapter = adapter;
    
        final net.mamoe.mirai.utils.BotConfiguration configuration = miraiBot.getConfiguration();
        configuration.noBotLog();
    }
    
    @Override
    protected void setupBotConfiguration(BotConfiguration botConfiguration) {
        // set working directory
        final net.mamoe.mirai.utils.BotConfiguration configuration = miraiBot.getConfiguration();
        final File elderWorkingDirectory = configuration.getWorkingDir();
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
            configuration.setBotLoggerSupplier(bot -> new MiraiLogger(this));
            configuration.setNetworkLoggerSupplier(bot -> new MiraiLogger(this));
        }
    }
    
    @Override
    public void start0() {
        if (!miraiBot.isOnline()) {
            miraiBot.login();
        }
        miraiBot.getEventChannel().registerListenerHost(new EventForwarder(this));
    }
    
    @Override
    public void stop0() {
        if (!adapter) {
            miraiBot.close();
        }
    }
    
    @Override
    public Friend getSelf() {
        return new MiraiFriend(this, miraiBot.getAsFriend());
    }
    
    @Override
    public List<Friend> getFriends() {
        return Collections.unmodifiableList(
            miraiBot.getFriends()
                .stream()
                .map(x -> new MiraiFriend(MiraiBot.this, x))
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public Friend getFriend(Code code) {
        Codes.requiredLongCode(code);
    
        final net.mamoe.mirai.contact.Friend friend = miraiBot.getFriend(((LongCode) code).getCode());
        if (Objects.nonNull(friend)) {
            return new MiraiFriend(this, friend);
        }
        
        return null;
    }
    
    @Override
    public List<Scope> getScopes() {
        return Collections.unmodifiableList(
            miraiBot.getGroups()
                .stream()
                .map(group -> new MiraiGroup(MiraiBot.this, group))
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public Scope getScope(Code code) {
        Codes.requiredLongCode(code);
        
        final long scopeCode = ((LongCode) code).getCode();
        final Group group = miraiBot.getGroup(scopeCode);
        
        if (Objects.isNull(group)) {
            return null;
        } else {
            return new MiraiGroup(this, group);
        }
    }
    
    @Override
    public InstantMessenger getInstantMessenger() {
        return InstantMessenger.QQ;
    }
}
