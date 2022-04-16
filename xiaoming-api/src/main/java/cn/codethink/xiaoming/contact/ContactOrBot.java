package cn.codethink.xiaoming.contact;

import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.code.Code;

/**
 * 会话或 Bot
 *
 * @author Chuanwise
 */
public interface ContactOrBot
    extends BotObject {
    
    /**
     * 获取头像 URL
     *
     * @return 头像 URL
     */
    String getAvatarUrl();
    
    /**
     * 获取目标账号码
     *
     * @return 目标账号码
     */
    Code getCode();
}
