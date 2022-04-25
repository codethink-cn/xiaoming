package cn.codethink.xiaoming.contact;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import net.mamoe.mirai.contact.AnonymousMember;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.contact.AnonymousSender
 */
public class QqAnonymousSender
    implements AnonymousGroupSender {
    
    private final QqGroup group;
    
    private final AnonymousMember anonymousMember;
    
    protected volatile boolean available = true;
    
    public QqAnonymousSender(QqGroup group, AnonymousMember anonymousMember) {
        Preconditions.objectNonNull(group, "group");
        Preconditions.objectNonNull(anonymousMember, "anonymous member");
        
        this.group = group;
        this.anonymousMember = anonymousMember;
    }
    
    @Override
    public Bot getBot() {
        return group.getBot();
    }
    
    @Override
    public boolean isAvailable() {
        return available;
    }
    
    @Override
    public QqGroup getMass() {
        return group;
    }
    
    @Override
    public String getMassNick() {
        return anonymousMember.getNick();
    }
    
    @Override
    public String getAvatarUrl() {
        return anonymousMember.getAvatarUrl();
    }
    
    @Override
    public String getSenderName() {
        return anonymousMember.getNick();
    }
}
