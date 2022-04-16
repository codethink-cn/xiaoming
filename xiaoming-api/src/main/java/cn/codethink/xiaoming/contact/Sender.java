package cn.codethink.xiaoming.contact;

import cn.codethink.xiaoming.BotObject;

/**
 * 某种可以发送消息的对象
 *
 * @author Chuanwise
 */
public interface Sender
    extends BotObject {
    
    /**
     * 获取头像 URL
     *
     * @return 头像 URL
     */
    String getAvatarUrl();
    
    /**
     * 获取作为发送方的名称。
     * 当发送方为集体成员时，返回集体昵称 {@link GroupSender#getMassNick()}
     * 当发送方为私聊时，返回备注名 {@link Friend#getRemarkName()}
     *
     * @return 名字
     */
    String getSenderName();
}
