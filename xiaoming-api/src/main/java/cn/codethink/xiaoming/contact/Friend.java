package cn.codethink.xiaoming.contact;

/**
 * 表示某位好友，可以用于发起私聊会话。
 *
 * @author Chuanwise
 */
public interface Friend
    extends Contact, Cached, User {
    
    /**
     * 删除好友
     */
    void delete();
}