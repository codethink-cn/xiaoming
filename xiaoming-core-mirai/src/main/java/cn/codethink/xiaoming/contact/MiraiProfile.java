package cn.codethink.xiaoming.contact;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;
import net.mamoe.mirai.data.UserProfile;

/**
 * @author Chuanwise
 */
public class MiraiProfile
    extends AbstractBotObject
    implements Profile {
    
    private final UserProfile profile;
    
    public MiraiProfile(Bot bot, UserProfile profile) {
        super(bot);
    
        Preconditions.nonNull(profile, "profile");
        
        this.profile = profile;
    }
    
    @Override
    public Sex getSex() {
        return MiraiSex.fromMirai(profile.getSex());
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
