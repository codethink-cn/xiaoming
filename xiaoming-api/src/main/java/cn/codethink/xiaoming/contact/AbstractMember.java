package cn.codethink.xiaoming.contact;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import lombok.Getter;

/**
 * @see Member
 * @author Chuanwise
 */
@Getter
public abstract class AbstractMember
    extends AbstractContact
    implements Member {
    
    /**
     * 账号所在的集体
     */
    protected final Mass mass;
    
    public AbstractMember(Mass mass) {
        super(mass.getBot());
        Preconditions.objectNonNull(mass, "mass");
        
        this.mass = mass;
    }
    
    @Override
    public Friend asFriend() {
        return bot.getFriend(getCode());
    }
    
    @Override
    public Stranger asStranger() {
        return bot.getStranger(getCode());
    }
}
