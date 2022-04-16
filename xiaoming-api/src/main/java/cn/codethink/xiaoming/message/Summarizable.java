package cn.codethink.xiaoming.message;

/**
 * 可以被序列化为消息码的对象
 *
 * @author Chuanwise
 */
public interface Summarizable {
    
    /**
     * 获取描述消息内容的字符串
     *
     * @return 描述消息内容的字符串
     */
    String serializeToSummary();
}
