package cn.codethink.xiaoming.contact;

import cn.codethink.common.util.Preconditions;
import cn.codethink.common.util.StaticUtilities;
import net.mamoe.mirai.data.UserProfile;

import java.util.NoSuchElementException;

/**
 * @see Sex
 * @author Chuanwise
 */
public class MiraiSex
    extends StaticUtilities {
    
    /**
     * 将 Mirai 性别转化为小明性别
     * @param sex Mirai 性别
     * @return 小明性别
     */
    public static Sex fromMirai(UserProfile.Sex sex) {
        Preconditions.nonNull(sex, "sex");
        
        switch (sex) {
            case MALE:
                return Sex.MALE;
            case FEMALE:
                return Sex.FEMALE;
            case UNKNOWN:
                return Sex.SECRET;
            default:
                throw new NoSuchElementException();
        }
    }
    
    /**
     * 将小明性别转化为 Mirai 性别
     *
     * @param sex 小明性别
     * @return Mirai 性别
     */
    public static UserProfile.Sex toMirai(Sex sex) {
        Preconditions.nonNull(sex, "sex");
        
        switch (sex) {
            case MALE:
                return UserProfile.Sex.MALE;
            case FEMALE:
                return UserProfile.Sex.FEMALE;
            case SECRET:
                return UserProfile.Sex.UNKNOWN;
            default:
                throw new NoSuchElementException();
        }
    }
}
