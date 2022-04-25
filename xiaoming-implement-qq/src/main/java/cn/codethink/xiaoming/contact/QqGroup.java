package cn.codethink.xiaoming.contact;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.AbstractBot;
import cn.codethink.xiaoming.QqBot;
import cn.codethink.xiaoming.annotation.InternalAPI;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.code.LongCode;
import cn.codethink.xiaoming.exception.NoSuchGroupException;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.receipt.MessageReceipt;
import cn.codethink.xiaoming.util.CachedContactMap;
import cn.codethink.xiaoming.util.QqContacts;
import lombok.Getter;
import net.mamoe.mirai.contact.AnonymousMember;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;

import java.util.Collections;
import java.util.Map;

/**
 * qq 的 Friend
 *
 * @author Chuanwise
 */
@InternalAPI
@SuppressWarnings("all")
@Getter
public class QqGroup
    extends AbstractGroup
    implements QqContact {
    
    /**
     * Qq 群
     */
    @Getter
    protected final Group qqGroup;
    
    /**
     * 群号
     */
    private final LongCode code;
    
    /**
     * 群设置项
     */
    private final QqGroupConfiguration configuration;
    
    /**
     * 群聊实例缓存
     */
    protected final CachedContactMap<NormalMember, QqMember> members = new CachedContactMap<>(
        normalMember -> new QqMember(this, normalMember),
        qqGroup -> qqGroup.available = true,
        qqGroup -> qqGroup.available = false,
        normalMember -> Code.ofLong(normalMember.getId())
    );
    
    /**
     * 匿名用户实例缓存
     */
    protected final CachedContactMap<AnonymousMember, QqAnonymousSender> anonymous = new CachedContactMap<>(
        anonymousMember -> new QqAnonymousSender(this, anonymousMember),
        anonymousSender -> anonymousSender.available = true,
        anonymousSender -> anonymousSender.available = false,
        anonymousMember -> Code.ofString(anonymousMember.getAnonymousId())
    );
    
    private final QqMember botAsMember;
    
    public QqGroup(QqBot bot, Group qqGroup) {
        super(bot);
        Preconditions.nonNull(qqGroup, "group");
        
        this.qqGroup = qqGroup;
        this.code = LongCode.valueOf(qqGroup.getId());
        
        this.configuration = new QqGroupConfiguration(this);
        
        final ContactList<NormalMember> members = qqGroup.getMembers();
        final Map<Code, QqMember> membersAvailable = this.members.getAvailable();
        for (NormalMember member : members) {
            membersAvailable.put(Code.ofLong(member.getId()), new QqMember(this, member));
        }
    
        // bot as member
        final NormalMember botAsMember = qqGroup.getBotAsMember();
        this.botAsMember = new QqMember(this, botAsMember);
        this.members.getAvailable().put(bot.getCode(), this.botAsMember);
    }
    
    @Override
    public Group getQqContact() {
        return qqGroup;
    }
    
    @Override
    public MessageReceipt sendMessage(Message message) {
        Preconditions.nonNull(message, "message content");
        final QqBot bot = (QqBot) this.bot;
    
        if (isAvailable()) {
            return QqContacts.sendGroupMessage(message, this);
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
    public QqMember getMember(Code code) {
        Preconditions.objectNonNull(code, "code");
    
        return members.getAvailable(code);
    }
    
    @Override
    public QqMember getMemberOrFail(Code code) {
        return (QqMember) super.getMemberOrFail(code);
    }
    
    @Override
    public QqAnonymousSender getAnonymousSender(Code code) {
        return anonymous.getAvailable(code);
    }
    
    @Override
    public QqAnonymousSender getAnonymousSenderOrFail(Code code) {
        return (QqAnonymousSender) super.getAnonymousSenderOrFail(code);
    }
    
    public Map<Code, QqAnonymousSender> getAnonymousSenders() {
        return anonymous.getAvailable();
    }
    
    @Override
    public QqMember getBotAsMember() {
        return botAsMember;
    }
    
    @Override
    public String getName() {
        return qqGroup.getName();
    }
    
    @Override
    public String getAvatarUrl() {
        assertBotIsInGroup();
        return qqGroup.getAvatarUrl();
    }
    
    @Override
    public QqMember getOwner() {
        return members.getAvailable(Code.ofLong(qqGroup.getOwner().getId()));
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
    public boolean quit() {
        return qqGroup.quit();
    }
    
    @Override
    public Code getCode() {
        return code;
    }
}
