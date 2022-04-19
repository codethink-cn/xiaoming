package cn.codethink.xiaoming.contact;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.util.Qqs;
import net.mamoe.mirai.data.UserProfile;

/**
 * @author Chuanwise
 */
public class QqProfile
    extends AbstractBotObject
    implements Profile {
    
    private final UserProfile profile;
    
    public QqProfile(Bot bot, UserProfile profile) {
        super(bot);
    
        Preconditions.nonNull(profile, "profile");
        
        this.profile = profile;
    }
    
    @Override
    public Sex getSex() {
        return Qqs.toXiaoMing(profile.getSex());
    }
    
    @Override
    public int getAge() {
        return profile.getAge();
    }
    
    @Override
    public int getLevel() {
        return profile.getQLevel();
    }
    
    @Override
    public String getSign() {
        return profile.getSign();
    }
    
    @Override
    public String getEmail() {
        return profile.getEmail();
    }
}
