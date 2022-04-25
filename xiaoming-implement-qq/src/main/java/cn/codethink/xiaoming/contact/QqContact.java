package cn.codethink.xiaoming.contact;

/**
 * 使用 qq 实现的会话
 *
 * @author Chuanwise
 */
public interface QqContact
    extends Contact {
    
    /**
     * 获取 qq 的会话
     *
     * @return qq 的会话
     */
    net.mamoe.mirai.contact.Contact getQqContact();
}
