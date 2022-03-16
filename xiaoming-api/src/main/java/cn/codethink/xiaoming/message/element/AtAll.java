package cn.codethink.xiaoming.message.element;

import cn.codethink.xiaoming.message.element.AbstractMessageElement;
import cn.codethink.xiaoming.message.element.At;

/**
 * 用于 @ 全体成员的 AtAll
 *
 * @author Chuanwise
 */
public class AtAll
        extends AbstractMessageElement
        implements At {
    
    @SuppressWarnings("all")
    public static final String MESSAGE_CODE = "[atall]";
    
    public static final String CONTENT = "@全体成员";
    
    private static final AtAll INSTANCE = new AtAll();
    
    public static AtAll getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String toMessageCode() {
        return MESSAGE_CODE;
    }
    
    @Override
    public String toContent() {
        return CONTENT;
    }
}
