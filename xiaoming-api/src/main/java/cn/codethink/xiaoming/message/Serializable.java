package cn.codethink.xiaoming.message;

/**
 * 可以被序列化为消息码的对象
 *
 * @author Chuanwise
 */
public interface Serializable {
    
    /**
     * 序列化为消息码
     *
     * @return 消息码
     */
    String serializeToMessageCode();
}
