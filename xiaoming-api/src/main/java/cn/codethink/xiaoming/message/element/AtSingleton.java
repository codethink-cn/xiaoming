package cn.codethink.xiaoming.message.element;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.annotation.IMRelatedAPI;
import cn.codethink.xiaoming.code.Code;
import lombok.Data;

/**
 * 用于 @ 人的 At
 *
 * @author Chuanwise
 */
@Data
@IMRelatedAPI
public class AtSingleton
    extends AbstractBasicMessage
    implements At, BasicMessage {
    
    /**
     * @ 指向的目标
     */
    private final Code targetCode;
    
    /**
     * 构造一个 @ 人的消息
     *
     * @param targetCode 目标码
     * @throws NullPointerException targetCode 为 null
     */
    public AtSingleton(Code targetCode) {
        Preconditions.objectNonNull(targetCode, "target code");
        
        this.targetCode = targetCode;
    }
    
    @Override
    public String serializeToMessageCode() {
        return "[at:singleton," + targetCode + "]";
    }
    
    @Override
    public String serializeToSummary() {
        return "@" + targetCode.asString();
    }
}
