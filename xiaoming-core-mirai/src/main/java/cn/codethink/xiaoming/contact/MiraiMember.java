package cn.codethink.xiaoming.contact;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.annotation.InternalAPI;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.code.LongCode;
import cn.codethink.xiaoming.event.*;
import cn.codethink.xiaoming.exception.NoSuchFriendException;
import cn.codethink.xiaoming.exception.NoSuchMemberException;
import cn.codethink.xiaoming.exception.NotYetImplementedException;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.MiraiMessageChain;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.receipt.MessageReceipt;
import cn.codethink.xiaoming.message.reference.*;
import cn.codethink.xiaoming.util.MiraiContacts;
import lombok.Data;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.contact.Stranger;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * mirai 的 Member
 *
 * @author Chuanwise
 */
@InternalAPI
@SuppressWarnings("all")
@Data
public class MiraiMember
    extends AbstractMember
    implements GroupMember {
    
    /**
     * Mirai 群成员
     */
    private final net.mamoe.mirai.contact.NormalMember miraiMember;
    
    /**
     * 账户所在的群
     */
    private final MiraiGroup group;
    
    /**
     * 个人资料
     */
    private final MiraiProfile profile;
    
    /**
     * QQ
     */
    private final LongCode code;
    
    protected MiraiMember(MiraiGroup group,
                          net.mamoe.mirai.contact.NormalMember miraiMember) {
        super(group);
        Preconditions.nonNull(miraiMember, "member");
        
        this.group = group;
        this.code = (LongCode) Code.ofLong(miraiMember.getId());
    
        this.miraiMember = miraiMember;
        this.profile = new MiraiProfile(getBot(), miraiMember.queryProfile());
    }
    
//    {
//        miraiMember.getLastSpeakTimestamp();
//
//        AnonymousMember member;
//        MessageReference source;
//
//    }
    
    public boolean isAvailable() {
        return group.isAvailable() && available;
    }
    
    @Override
    public MiraiGroup getMass() {
        return group;
    }
    
    @Override
    public void setSpecialTitle(String title) {
        miraiMember.setSpecialTitle(title);
    }
    
    @Override
    public String getMassNick() {
        return miraiMember.getNameCard();
    }
    
    @Override
    public long getJoinTimestamp() {
        return TimeUnit.SECONDS.toMillis(miraiMember.getJoinTimestamp());
    }
    
    @Override
    public void setMassNick(String massNick) {
        miraiMember.setNameCard(massNick);
    }
    
    @Override
    public Role getRole() {
        return MiraiRole.fromMirai(miraiMember.getPermission());
    }
    
    @Override
    public void setRole(Role role) {
        Preconditions.objectNonNull(role, "role");
    
        switch (role) {
            case ADMIN:
                miraiMember.modifyAdmin(false);
                break;
            case MEMBER:
                miraiMember.modifyAdmin(false);
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
            miraiMember.unmute();
        } else {
            miraiMember.mute((int) timeUnit.toSeconds(time));
        }
    }
    
    @Override
    public void unmute() {
        miraiMember.unmute();
    }
    
    @Override
    public long getMuteTimeRemaining() {
        return TimeUnit.SECONDS.toMinutes(miraiMember.getMuteTimeRemaining());
    }
    
    @Override
    public boolean isMuted() {
        return miraiMember.isMuted();
    }
    
    @Override
    public String getAvatarUrl() {
        return miraiMember.getAvatarUrl();
    }
    
    @Override
    public String getSenderName() {
        return miraiMember.getNameCard();
    }
    
    @Override
    public String getRemarkName() {
        return miraiMember.getRemark();
    }
    
    @Override
    public String getAccountName() {
        return miraiMember.getNick();
    }
    
    @Override
    public MiraiProfile getProfile() {
        return profile;
    }
    
    @Override
    public LongCode getCode() {
        return code;
    }
    
    @Override
    public MiraiFriend asFriend() {
        return (MiraiFriend) super.asFriend();
    }
    
    @Override
    public void flapInGroup() {
        miraiMember.nudge().sendTo(miraiMember.getGroup());
    }
    
    @Override
    public void flapInMember() {
        miraiMember.nudge().sendTo(miraiMember);
    }
    
    @Override
    public MessageReceipt sendMessage(Message message) {
        Preconditions.nonNull(message, "message");
        final MiraiBot bot = (MiraiBot) this.bot;
        
        // send as friend
        final MiraiFriend friend = asFriend();
        if (Objects.nonNull(friend)) {
            return MiraiContacts.sendFriendMessage(message, friend);
        }
    
        // send as group member
        if (isAvailable()) {
            return MiraiContacts.sendGroupMemberMessage(message, this);
        }
        
        // send as others group member
        // find him in all groups
        // try to send member message
        // because there's no sendable checking before send
        // so if member sending is banned in this group
        // bot will be banned immediately
        // TODO: 2022/4/16 do sendable checking before send after mirai updated
    
        final Collection<MiraiGroup> masses = bot.getMasses().values();
        for (MiraiGroup mass : masses) {
            if (masses == this) {
                continue;
            }
            final MiraiMember member = mass.getMember(code);
            if (Objects.isNull(member)) {
                continue;
            }
    
            return MiraiContacts.sendGroupMemberMessage(message, member);
        }
    
        // if he's not a member of joined groups
        // find as stranger
        final MiraiStranger stranger = asStranger();
        if (Objects.nonNull(stranger)) {
            return MiraiContacts.sendStrangerMessage(message, stranger);
        }
    
        throw new NoSuchMemberException(mass, code);
    }
    
    @Override
    public MiraiStranger asStranger() {
        return (MiraiStranger) super.asStranger();
    }
}