package cn.codethink.xiaoming.message.element;

/**
 * 用于 @ 全体成员的 AtAll
 *
 * @author Chuanwise
 */
public class AtAll
    extends AbstractBasicMessage
    implements At, BasicMessage {
    
    /**
     * 消息码
     */
    private static final String MESSAGE_CODE = "[at:all]";
    
    /**
     * AtAll 的全局唯一单例
     */
    public static final AtAll INSTANCE = new AtAll();
    
    /**
     * 摘要消息
     */
    private static final String SUMMARY = "@全体成员";
    
    private AtAll() {
    }
    
    @Override
    public String serializeToMessageCode() {
        return MESSAGE_CODE;
    }
    
    @Override
    public String serializeToSummary() {
        return SUMMARY;
    }
}
