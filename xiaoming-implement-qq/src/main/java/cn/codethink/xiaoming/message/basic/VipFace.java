package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.message.MessageCodeBuilder;
import lombok.Data;

/**
 * Vip 表情
 *
 * @author Chuanwise
 */
@Data
public class VipFace
    extends AbstractBasicMessage
    implements SingletonMessage, AutoSerializable, AutoSummarizable {
    
    /**
     * Vip 表情类型
     */
    private final VipFaceType type;
    
    /**
     * Vip 表情数
     */
    private final int count;
    
    public VipFace(VipFaceType type, int count) {
        Preconditions.objectNonNull(type, "type");
        Preconditions.argument(count > 0, "count must be bigger than 0!");
        
        this.type = type;
        this.count = count;
    }
}
