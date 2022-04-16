package cn.codethink.xiaoming.contact;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.AbstractBot;
import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.annotation.InternalAPI;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.code.LongCode;
import cn.codethink.xiaoming.concurrent.BotFuture;
import cn.codethink.xiaoming.exception.NoSuchGroupException;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.receipt.MessageReceipt;
import cn.codethink.xiaoming.util.CachedContactMap;
import cn.codethink.xiaoming.util.MiraiContacts;
import lombok.Getter;
import net.mamoe.mirai.contact.AnonymousMember;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;

import java.util.Collections;
import java.util.Map;

/**
 * mirai 的 Friend
 *
 * @author Chuanwise
 */
@InternalAPI
@SuppressWarnings("all")
@Getter
public class MiraiGroup
    extends AbstractGroup {
    
    /**
     * Mirai 群
     */
    @Getter
    protected final Group miraiGroup;
    
    /**
     * 群号
     */
    private final LongCode code;
    
    /**
     * 群设置项
     */
    private final MiraiGroupConfiguration configuration;
    
    /**
     * 群聊实例缓存
     */
    protected final CachedContactMap<NormalMember, MiraiMember> members = new CachedContactMap<>(
        normalMember -> new MiraiMember(this, normalMember),
        miraiGroup -> miraiGroup.available = true,
        miraiGroup -> miraiGroup.available = false,
        normalMember -> Code.ofLong(normalMember.getId())
    );
    
    /**
     * 匿名用户实例缓存
     */
    protected final CachedContactMap<AnonymousMember, MiraiAnonymousSender> anonymous = new CachedContactMap<>(
        anonymousMember -> new MiraiAnonymousSender(this, anonymousMember),
        anonymousSender -> anonymousSender.available = true,
        anonymousSender -> anonymousSender.available = false,
        anonymousMember -> Code.ofString(anonymousMember.getAnonymousId())
    );
    
    private final MiraiMember botAsMember;
    
    public MiraiGroup(MiraiBot bot, Group miraiGroup) {
        super(bot);
        Preconditions.nonNull(miraiGroup, "group");
        
        this.miraiGroup = miraiGroup;
        this.code = LongCode.valueOf(miraiGroup.getId());
        
        this.configuration = new MiraiGroupConfiguration(this);
        
        final ContactList<NormalMember> members = miraiGroup.getMembers();
        final Map<Code, MiraiMember> membersAvailable = this.members.getAvailable();
        for (NormalMember member : members) {
            membersAvailable.put(Code.ofLong(member.getId()), new MiraiMember(this, member));
        }
    
        // bot as member
        final NormalMember botAsMember = miraiGroup.getBotAsMember();
        this.botAsMember = new MiraiMember(this, botAsMember);
        this.members.getAvailable().put(bot.getCode(), this.botAsMember);
    }
    
    @Override
    public MessageReceipt sendMessage(Message message) {
        Preconditions.nonNull(message, "message content");
        final MiraiBot bot = (MiraiBot) this.bot;
    
        if (isAvailable()) {
            return MiraiContacts.sendGroupMessage(message, this);
        }
    
        throw new NoSuchGroupException(bot, code);
    }
    
    @Override
    public Map<Code, GroupMember> getMembers() {
        return Collections.unmodifiableMap(
            members.getAvailable()
        );
    }
    
    @Override
    public MiraiMember getMember(Code code) {
        Preconditions.objectNonNull(code, "code");
    
        return members.getAvailable(code);
    }
    
    @Override
    public MiraiMember getMemberOrFail(Code code) {
        return (MiraiMember) super.getMemberOrFail(code);
    }
    
    @Override
    public MiraiAnonymousSender getAnonymousSender(Code code) {
        return anonymous.getAvailable(code);
    }
    
    @Override
    public MiraiAnonymousSender getAnonymousSenderOrFail(Code code) {
        return (MiraiAnonymousSender) super.getAnonymousSenderOrFail(code);
    }
    
    public Map<Code, MiraiAnonymousSender> getAnonymousSenders() {
        return anonymous.getAvailable();
    }
    
    @Override
    public MiraiMember getBotAsMember() {
        return botAsMember;
    }
    
    @Override
    public String getName() {
        return miraiGroup.getName();
    }
    
    @Override
    public String getAvatarUrl() {
        assertBotIsInGroup();
        return miraiGroup.getAvatarUrl();
    }
    
    @Override
    public MiraiMember getOwner() {
        return members.getAvailable(Code.ofLong(miraiGroup.getOwner().getId()));
    }
    
    @Override
    public GroupConfiguration getConfiguration() {
        return configuration;
    }
    
    protected void assertBotIsInGroup() {
        if (!isAvailable()) {
            throw new NoSuchGroupException(bot, code);
        }
    }
    
    @Override
    public String toString() {
        return "Group(" +
            "code=" + code + "," +
            "name=" + getName() + ")";
    }
    
    @Override
    public BotFuture<Boolean> quit() {
        return ((AbstractBot) bot).getScheduler().submit(() -> miraiGroup.quit());
    }
    
    @Override
    public Code getCode() {
        return code;
    }
}
