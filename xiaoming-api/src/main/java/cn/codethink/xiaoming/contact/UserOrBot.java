package cn.codethink.xiaoming.contact;

import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.code.Code;

/**
 * 表示用户或 Bot 自身，用于代表消息发送方和目标区域。
 * 是 {@link User} 和 {@link cn.codethink.xiaoming.Bot} 的唯一公共接口。
 *
 * @author Chuanwise
 */
public interface UserOrBot
    extends BotObject {
    
    /**
     * 获取作为好友的备注名
     *
     * @return 备注名。如果不是好友，则返回 null
     */
    String getRemarkName();
    
    /**
     * 获取账户名
     *
     * @return 账户名
     */
    String getAccountName();
    
    /**
     * 获取账号信息
     *
     * @return 账号信息
     */
    Profile getProfile();
    
    /**
     * 获取账户码
     *
     * @return 账户码
     */
    Code getCode();
    
    /**
     * 获取作为好友的发送方
     *
     * @return 发送方，或 null
     */
    Friend asFriend();
    
    /**
     * 获取作为陌生人的发送方
     *
     * @return 陌生人，或 null
     */
    Stranger asStranger();
}
