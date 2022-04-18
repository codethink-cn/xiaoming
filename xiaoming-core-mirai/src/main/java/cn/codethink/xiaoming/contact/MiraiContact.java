package cn.codethink.xiaoming.contact;

/**
 * 使用 mirai 实现的会话
 *
 * @author Chuanwise
 */
public interface MiraiContact
    extends Contact {
    
    /**
     * 获取 mirai 的会话
     *
     * @return mirai 的会话
     */
    net.mamoe.mirai.contact.Contact getMiraiContact();
}
