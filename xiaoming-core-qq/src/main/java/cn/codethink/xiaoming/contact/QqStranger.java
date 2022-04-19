package cn.codethink.xiaoming.contact;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.QqBot;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.receipt.MessageReceipt;
import lombok.Getter;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Stranger;

import java.util.Objects;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.contact.Stranger
 */
@Getter
public class QqStranger
    extends AbstractStranger
    implements QqContact {
    
    private final Stranger qqStranger;
    
    private QqProfile profile;
    
    private final Code code;
    
    public QqStranger(QqBot qqBot, Stranger stranger) {
        super(qqBot);
    
        Preconditions.objectNonNull(stranger, "stranger");
        
        this.qqStranger = stranger;
        this.code = Code.ofLong(stranger.getId());
    }
    
    @Override
    public MessageReceipt sendMessage(Message message) {
        // TODO: 2022/4/16 send message
        return null;
    }
    
    @Override
    public Contact getQqContact() {
        return qqStranger;
    }
    
    @Override
    public String getAvatarUrl() {
        return qqStranger.getAvatarUrl();
    }
    
    @Override
    public String getSenderName() {
        return qqStranger.getNick();
    }
    
    @Override
    public String getRemarkName() {
        return null;
    }
    
    @Override
    public String getAccountName() {
        return qqStranger.getNick();
    }
    
    @Override
    public Profile getProfile() {
        if (Objects.isNull(profile)) {
            profile = new QqProfile(getBot(), qqStranger.queryProfile());
        }
        return profile;
    }
    
    @Override
    public Code getCode() {
        return code;
    }
    
    @Override
    public QqFriend asFriend() {
        return (QqFriend) super.asFriend();
    }
    
    @Override
    public QqStranger asStranger() {
        return this;
    }
}
