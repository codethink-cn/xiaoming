package cn.codethink.xiaoming.contact;

/**
 * 对某个人的私聊会话。
 *
 *
 *
 * @author Chuanwise
 */
public interface Friend
        extends Contact {
    
    /**
     * 检测该用户现在是否是朋友
     *
     * @return 该用户现在是否是朋友
     */
    boolean isFriendNow();
    
    /**
     * 获取头像 URL
     *
     * @return 头像 URL
     */
    String getAvatarUrl();
}
