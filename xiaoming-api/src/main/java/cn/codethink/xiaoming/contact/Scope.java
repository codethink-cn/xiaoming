package cn.codethink.xiaoming.contact;

import cn.codethink.xiaoming.code.Code;

import java.net.URL;
import java.util.List;

/**
 * 表示一群人的范围，如群聊、频道和服务器。
 *
 * @author Chuanwise
 */
public interface Scope {
    
    /**
     * 获取头像 URL
     *
     * @return 头像 URL
     */
    String getAvatarUrl();
    
    /**
     * 获取范围名
     *
     * @return 范围名
     */
    String getName();
    
    /**
     * 获取范围内的成员
     *
     * @return 成员
     */
    List<Member> getMembers();
    
    /**
     * 获取范围内的某个成员
     *
     * @param code 成员编号
     * @return 当找到该成员，返回该成员，否则返回 null
     */
    Member getMember(Code code);
    
    /**
     * 获得作为成员的 Bot 自己
     * @return Bot 自己
     */
    Member getBotSelf();
    
    /**
     * 获得范围的主人
     *
     * @return 范围的主人
     */
    Member getOwner();
}
