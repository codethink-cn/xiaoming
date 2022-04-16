package cn.codethink.xiaoming.message.basic;

/**
 * 实现层 Bot 消息
 *
 * @author Chuanwise
 */
public interface Origin
    extends BasicMessage {
    
    /**
     * 获取实现层消息码
     *
     * @return 实现层消息码
     */
    String getOriginalCode();
}
