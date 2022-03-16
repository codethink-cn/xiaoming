package cn.codethink.xiaoming.contact;

/**
 * 对某个人的私聊会话。
 *
 *
 *
 * @author Chuanwise
 */
public interface Member
        extends Contact {
    
    /**
     * 检测该用户现在是否是朋友
     *
     * @return 该用户现在是否是朋友
     */
    boolean isFriendNow();
    
    /**
     * 检测该用户现在是否是成员
     *
     * @return 该用户现在是否是成员
     */
    boolean isMemberNow();
}
