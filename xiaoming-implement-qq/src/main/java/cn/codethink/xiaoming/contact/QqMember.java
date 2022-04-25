package cn.codethink.xiaoming.contact;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.QqBot;
import cn.codethink.xiaoming.annotation.InternalAPI;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.code.LongCode;
import cn.codethink.xiaoming.exception.NoSuchMemberException;
import cn.codethink.xiaoming.exception.NotYetImplementedException;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.receipt.MessageReceipt;
import cn.codethink.xiaoming.util.QqContacts;
import cn.codethink.xiaoming.util.Qqs;
import lombok.Getter;
import net.mamoe.mirai.contact.NormalMember;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * qq 的 Member
 *
 * @author Chuanwise
 */
@Getter
@InternalAPI
@SuppressWarnings("all")
public class QqMember
    extends AbstractMember
    implements GroupMember, QqContact {
    
    /**
     * Qq 群成员
     */
    private final net.mamoe.mirai.contact.NormalMember qqMember;
    
    /**
     * 账户所在的群
     */
    private final QqGroup group;
    
    /**
     * 个人资料
     */
    private QqProfile profile;
    
    /**
     * QQ
     */
    private final LongCode code;
    
    protected QqMember(QqGroup group,
                       net.mamoe.mirai.contact.NormalMember qqMember) {
        super(group);
        Preconditions.nonNull(qqMember, "member");
        
        this.group = group;
        this.code = (LongCode) Code.ofLong(qqMember.getId());
    
        this.qqMember = qqMember;
    }
    
//    {
//        qqMember.getLastSpeakTimestamp();
//
//        AnonymousMember member;
//        MessageSource source;
    // TODO: 2022/4/18 clear
//
//    }
    
    
    @Override
    public NormalMember getQqContact() {
        return qqMember;
    }
    
    public boolean isAvailable() {
        return group.isAvailable() && available;
    }
    
    @Override
    public QqGroup getMass() {
        return group;
    }
    
    @Override
    public void setSpecialTitle(String title) {
        qqMember.setSpecialTitle(title);
    }
    
    @Override
    public String getMassNick() {
        return qqMember.getNameCard();
    }
    
    @Override
    public long getJoinTimestamp() {
        return TimeUnit.SECONDS.toMillis(qqMember.getJoinTimestamp());
    }
    
    @Override
    public void setMassNick(String massNick) {
        qqMember.setNameCard(massNick);
    }
    
    @Override
    public Role getRole() {
        return Qqs.toXiaoMing(qqMember.getPermission());
    }
    
    @Override
    public void setRole(Role role) {
        Preconditions.objectNonNull(role, "role");
    
        switch (role) {
            case ADMIN:
                qqMember.modifyAdmin(false);
                break;
            case MEMBER:
                qqMember.modifyAdmin(false);
                break;
            case OWNER:
                throw new NotYetImplementedException(getBot());
            default:
                throw new NoSuchElementException();
        }
    }
    
    @Override
    public void mute(long time) {
        mute(time, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void mute(long time, TimeUnit timeUnit) {
        Preconditions.objectNonNull(timeUnit, "time unit");
        Preconditions.argument(time >= 0, "mute time must be bigger than or equals to 0!");
    
        if (time == 0) {
            qqMember.unmute();
        } else {
            qqMember.mute((int) timeUnit.toSeconds(time));
        }
    }
    
    @Override
    public void unmute() {
        qqMember.unmute();
    }
    
    @Override
    public long getMuteTimeRemaining() {
        return TimeUnit.SECONDS.toMinutes(qqMember.getMuteTimeRemaining());
    }
    
    @Override
    public boolean isMuted() {
        return qqMember.isMuted();
    }
    
    @Override
    public String getAvatarUrl() {
        return qqMember.getAvatarUrl();
    }
    
    @Override
    public String getSenderName() {
        return qqMember.getNameCard();
    }
    
    @Override
    public String getRemarkName() {
        return qqMember.getRemark();
    }
    
    @Override
    public String getAccountName() {
        return qqMember.getNick();
    }
    
    @Override
    public QqProfile getProfile() {
        if (Objects.isNull(profile)) {
            profile = new QqProfile(getBot(), qqMember.queryProfile());
        }
        return profile;
    }
    
    @Override
    public LongCode getCode() {
        return code;
    }
    
    @Override
    public QqFriend asFriend() {
        return (QqFriend) super.asFriend();
    }
    
    @Override
    public void flapInGroup() {
        qqMember.nudge().sendTo(qqMember.getGroup());
    }
    
    @Override
    public void flapInMember() {
        qqMember.nudge().sendTo(qqMember);
    }
    
    @Override
    public MessageReceipt sendMessage(Message message) {
        Preconditions.nonNull(message, "message");
        final QqBot bot = (QqBot) this.bot;
        
        // send as friend
        final QqFriend friend = asFriend();
        if (Objects.nonNull(friend)) {
            return QqContacts.sendFriendMessage(message, friend);
        }
    
        // send as group member
        if (isAvailable()) {
            return QqContacts.sendGroupMemberMessage(message, this);
        }
        
        // send as others group member
        // find him in all groups
        // try to send member message
        // because there's no sendable checking before send
        // so if member sending is banned in this group
        // bot will be banned immediately
        // TODO: 2022/4/16 do sendable checking before send after qq updated
    
        final Collection<QqGroup> masses = bot.getMasses().values();
        for (QqGroup mass : masses) {
            if (masses == this) {
                continue;
            }
            final QqMember member = mass.getMember(code);
            if (Objects.isNull(member)) {
                continue;
            }
    
            return QqContacts.sendGroupMemberMessage(message, member);
        }
    
        // if he's not a member of joined groups
        // find as stranger
        final QqStranger stranger = asStranger();
        if (Objects.nonNull(stranger)) {
            return QqContacts.sendStrangerMessage(message, stranger);
        }
    
        throw new NoSuchMemberException(mass, code);
    }
    
    @Override
    public QqStranger asStranger() {
        return (QqStranger) super.asStranger();
    }
}