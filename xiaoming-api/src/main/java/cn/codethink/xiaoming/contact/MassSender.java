package cn.codethink.xiaoming.contact;

/**
 * 群聊里的消息发送方
 *
 * @author Chuanwise
 */
public interface MassSender
    extends Sender {
    
    /**
     * 获取所在的集体
     *
     * @return 成员所在的集体
     */
    Mass getMass();
    
    /**
     * 获取集体中的名片
     *
     * @return 集体中的名片
     */
    String getMassNick();
}
