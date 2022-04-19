package cn.codethink.xiaoming.contact;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.QqBot;
import cn.codethink.xiaoming.annotation.InternalAPI;
import cn.codethink.xiaoming.code.LongCode;
import cn.codethink.xiaoming.exception.NoSuchFriendException;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.util.QqContacts;
import lombok.Getter;
import net.mamoe.mirai.contact.Friend;

import java.util.Collection;
import java.util.Objects;

/**
 * qq 的 Friend
 *
 * @author Chuanwise
 */
@InternalAPI
@Getter
public class QqFriend
    extends AbstractFriend
    implements QqContact {
    
    /**
     * Qq 的 Friend
     */
    private final Friend qqFriend;
    
    /**
     * QQ
     */
    private final LongCode code;
    
    /**
     * 账户信息
     */
    private QqProfile profile;
    
    public QqFriend(QqBot bot, Friend qqFriend) {
        super(bot);
        Preconditions.nonNull(qqFriend, "qq friend");
        
        this.qqFriend = qqFriend;
        
        this.code = LongCode.valueOf(qqFriend.getId());
    }
    
    @Override
    public Friend getQqContact() {
        return qqFriend;
    }
    
    @Override
    public String getAccountName() {
        return qqFriend.getNick();
    }
    
    @Override
    public void delete() {
        qqFriend.delete();
    }
    
    @Override
    public String getRemarkName() {
        return qqFriend.getRemark();
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
        return qqFriend.getAvatarUrl();
    }
    
    @Override
    public String getSenderName() {
        return qqFriend.getNick();
    }
    
    @Override
    public QqProfile getProfile() {
        if (Objects.isNull(profile)) {
            profile = new QqProfile(getBot(), qqFriend.queryProfile());
        }
        return profile;
    }
    
    @Override
    public cn.codethink.xiaoming.message.receipt.MessageReceipt sendMessage(Message message) {
        Preconditions.nonNull(message, "message");
        final QqBot bot = (QqBot) this.bot;
        
        // try to send as friend
        if (isAvailable()) {
            return QqContacts.sendFriendMessage(message, this);
        }
    
        // if he is not friend now
        // find him in all groups
        // try to send member message
        // because there's no sendable checking before send
        // so if member sending is banned in this group
        // bot will be banned immediately
        // TODO: 2022/4/16 do sendable checking before send after qq updated
    
        final Collection<QqGroup> masses = bot.getMasses().values();
        for (QqGroup mass : masses) {
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
    
        throw new NoSuchFriendException(bot, code);
    }
    
    @Override
    public QqStranger asStranger() {
        return (QqStranger) super.asStranger();
    }
    
    @Override
    public QqFriend asFriend() {
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
