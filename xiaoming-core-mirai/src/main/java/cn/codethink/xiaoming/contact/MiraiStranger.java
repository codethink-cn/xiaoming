package cn.codethink.xiaoming.contact;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.receipt.MessageReceipt;
import lombok.Getter;
import net.mamoe.mirai.contact.Stranger;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.contact.Stranger
 */
@Getter
public class MiraiStranger
    extends AbstractStranger {
    
    private final Stranger miraiStranger;
    
    private final MiraiProfile profile;
    
    private final Code code;
    
    public MiraiStranger(MiraiBot miraiBot, Stranger stranger) {
        super(miraiBot);
    
        Preconditions.objectNonNull(stranger, "stranger");
        
        this.miraiStranger = stranger;
        this.code = Code.ofLong(stranger.getId());
        this.profile = new MiraiProfile(miraiBot, stranger.queryProfile());
    }
    
    @Override
    public MessageReceipt sendMessage(Message message) {
        // TODO: 2022/4/16 send message
        return null;
    }
    
    @Override
    public String getAvatarUrl() {
        return miraiStranger.getAvatarUrl();
    }
    
    @Override
    public String getSenderName() {
        return miraiStranger.getNick();
    }
    
    @Override
    public String getRemarkName() {
        return null;
    }
    
    @Override
    public String getAccountName() {
        return miraiStranger.getNick();
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
    public MiraiFriend asFriend() {
        return (MiraiFriend) super.asFriend();
    }
    
    @Override
    public MiraiStranger asStranger() {
        return this;
    }
}
