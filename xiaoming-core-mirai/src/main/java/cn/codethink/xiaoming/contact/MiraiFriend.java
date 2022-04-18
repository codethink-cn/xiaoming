package cn.codethink.xiaoming.contact;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.annotation.InternalAPI;
import cn.codethink.xiaoming.code.LongCode;
import cn.codethink.xiaoming.exception.NoSuchFriendException;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.util.MiraiContacts;
import lombok.Getter;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;

import java.util.Collection;
import java.util.Objects;

/**
 * mirai 的 Friend
 *
 * @author Chuanwise
 */
@InternalAPI
@Getter
public class MiraiFriend
    extends AbstractFriend
    implements MiraiContact {
    
    /**
     * Mirai 的 Friend
     */
    private final Friend miraiFriend;
    
    /**
     * QQ
     */
    private final LongCode code;
    
    /**
     * 账户信息
     */
    private MiraiProfile profile;
    
    public MiraiFriend(MiraiBot bot, Friend miraiFriend) {
        super(bot);
        Preconditions.nonNull(miraiFriend, "mirai friend");
        
        this.miraiFriend = miraiFriend;
        
        this.code = LongCode.valueOf(miraiFriend.getId());
    }
    
    @Override
    public Friend getMiraiContact() {
        return miraiFriend;
    }
    
    @Override
    public String getAccountName() {
        return miraiFriend.getNick();
    }
    
    @Override
    public void delete() {
        miraiFriend.delete();
    }
    
    @Override
    public String getRemarkName() {
        return miraiFriend.getRemark();
    }
    
    protected void assertAvailable() {
        if (!isAvailable()) {
            throw new NoSuchFriendException(bot, code);
        }
    }
    
    @Override
    public boolean isAvailable() {
        return available;
    }
    
    @Override
    public String getAvatarUrl() {
        assertAvailable();
        return miraiFriend.getAvatarUrl();
    }
    
    @Override
    public String getSenderName() {
        return miraiFriend.getNick();
    }
    
    @Override
    public MiraiProfile getProfile() {
        if (Objects.isNull(profile)) {
            profile = new MiraiProfile(getBot(), miraiFriend.queryProfile());
        }
        return profile;
    }
    
    @Override
    public cn.codethink.xiaoming.message.receipt.MessageReceipt sendMessage(Message message) {
        Preconditions.nonNull(message, "message");
        final MiraiBot bot = (MiraiBot) this.bot;
        
        // try to send as friend
        if (isAvailable()) {
            return MiraiContacts.sendFriendMessage(message, this);
        }
    
        // if he is not friend now
        // find him in all groups
        // try to send member message
        // because there's no sendable checking before send
        // so if member sending is banned in this group
        // bot will be banned immediately
        // TODO: 2022/4/16 do sendable checking before send after mirai updated
    
        final Collection<MiraiGroup> masses = bot.getMasses().values();
        for (MiraiGroup mass : masses) {
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
    
        throw new NoSuchFriendException(bot, code);
    }
    
    @Override
    public MiraiStranger asStranger() {
        return (MiraiStranger) super.asStranger();
    }
    
    @Override
    public MiraiFriend asFriend() {
        return this;
    }
    
    @Override
    public String toString() {
        return "Friend(" +
            "code=" + code + "," +
            "name=" + getAccountName() + "," +
            "remark=" + getRemarkName() + ")";
    }
}
