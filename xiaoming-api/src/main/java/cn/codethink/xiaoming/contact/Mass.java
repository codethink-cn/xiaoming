package cn.codethink.xiaoming.contact;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.concurrent.BotFuture;
import cn.codethink.xiaoming.exception.NoSuchMemberException;

import java.util.Map;
import java.util.Objects;

/**
 * 表示一个集体，如群聊、频道和服务器。
 *
 * @author Chuanwise
 */
public interface Mass
    extends BotObject, Cached {
    
    /**
     * 获取集体头像 URL
     *
     * @return 头像 URL
     */
    String getAvatarUrl();
    
    /**
     * 获取集体名
     *
     * @return 范围名
     */
    String getName();
    
    /**
     * 获取集体成员
     *
     * @return 成员
     */
    Map<Code, ? extends Member> getMembers();
    
    /**
     * 获取集体内的某个成员
     *
     * @param code 成员编号
     * @return 当找到该成员，返回该成员，否则返回 null
     * @throws NullPointerException code 为 null
     */
    Member getMember(Code code);
    
    /**
     * 获取集体内的某个成员
     *
     * @param code 成员编号
     * @return 成员
     * @throws NullPointerException code 为 null
     * @throws cn.codethink.xiaoming.exception.NoSuchMassException 找不到该成员
     */
    default Member getMemberOrFail(Code code) {
        Preconditions.objectNonNull(code, "code");
    
        final Member member = getMember(code);
        if (Objects.isNull(member)) {
            throw new NoSuchMemberException(this, code);
        }
        
        return member;
    }
    
    /**
     * 获得作为成员的 Bot 自己
     * @return Bot 自己
     */
    Member getBotAsMember();
    
    /**
     * 获得集体的主人
     *
     * @return 范围的主人
     */
    Member getOwner();
    
    /**
     * 退出集体
     *
     * @return 退出的 Future
     */
    BotFuture<Boolean> quit();
    
    /**
     * 获取集体号
     *
     * @return 集体号
     */
    Code getCode();
    
    /**
     * 获取集体设置
     *
     * @return 区域设置
     */
    MassConfiguration getConfiguration();
}
